package ru.itmo.spaceships.statistics.overall;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.BackpressureOverflowStrategy;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableSubscriber;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscription;
import ru.itmo.spaceships.model.SpaceShip;
import ru.itmo.spaceships.statistics.StatisticsCalculator;

import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static ru.itmo.spaceships.statistics.overall.OverallStatistics.DATE_FORMATTER;

@Slf4j
@RequiredArgsConstructor
public class RXBackpressureShipStatistics implements StatisticsCalculator<SpaceShip, OverallStatistics> {

	private final int bufferSize;
	private final boolean countDrops;

	@Override
	public OverallStatistics calculate(List<SpaceShip> objects) {
		ShipStatisticsSubscriber subscriber = new ShipStatisticsSubscriber(bufferSize);

		AtomicInteger droppedCount = new AtomicInteger(0);

		Flowable.fromIterable(objects).onBackpressureBuffer(
						bufferSize,
						() -> {
						},
						BackpressureOverflowStrategy.DROP_LATEST,
						ship -> {
							if (countDrops) {
								droppedCount.incrementAndGet();
							}
						}
				).subscribeOn(Schedulers.computation())
				.subscribe(subscriber);

		OverallStatistics result;
		try {
			result = subscriber.getStatisticsFuture().get(1, TimeUnit.HOURS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			log.error("Error occurred during overall statistics waiting", e);
			throw new RuntimeException(e);
		}

		if (countDrops && droppedCount.get() > 0) {
			log.info("Dropped {} ships", droppedCount.get());
		}

		return result;
	}

	@Slf4j
	@RequiredArgsConstructor
	private static class ShipStatisticsSubscriber implements FlowableSubscriber<SpaceShip> {

		private final int batchSize;
		private Subscription subscription;

		private final OverallStatistics statistics = new OverallStatistics();
		private int handledCount = 0;

		private final CompletableFuture<OverallStatistics> statisticsFuture = new CompletableFuture<>();

		@Override
		public void onSubscribe(@NonNull Subscription s) {
			this.subscription = s;

			handledCount = 0;
			s.request(batchSize);
		}

		@Override
		public void onNext(SpaceShip ship) {
			statistics.getCountByManufacturer().merge(ship.getManufacturer(), 1L, Long::sum);

			statistics.getCountByFuelType().merge(ship.getEngine().getFuelType().name(), 1L, Long::sum);

			statistics.getCountByDate().merge(DATE_FORMATTER.format(ship.getManufactureDate()), 1L, Long::sum);

			statistics.getAggregateMaxSpeed().accept(ship.getMaxSpeed());
			statistics.getAggregateCrewMembers().accept(ship.getCrew().size());
			statistics.getAggregateLength().accept(ship.getDimensions().length());

			statistics.getAggregateCrewByShipType().merge(ship.getType().name(),
					new LongSummaryStatistics(),
					(oldStats, newStats) -> {
						oldStats.accept(ship.getCrew().size());
						return oldStats;
					});

			if (++handledCount >= batchSize) {
				handledCount = 0;
				subscription.request(batchSize);
			}
		}

		@Override
		public void onError(Throwable throwable) {
			log.error("Error occurred when handling statistics", throwable);
			statisticsFuture.completeExceptionally(throwable);
		}

		@Override
		public void onComplete() {
			statisticsFuture.complete(statistics);
		}

		public Future<OverallStatistics> getStatisticsFuture() {
			return statisticsFuture;
		}
	}
}
