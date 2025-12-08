package ru.itmo.spaceships.statistics.manufacturer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.BackpressureOverflowStrategy;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableSubscriber;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscription;
import ru.itmo.spaceships.model.SpaceShipEntity;
import ru.itmo.spaceships.statistics.StatisticsCalculator;

/**
 * Класс для сбора статистики о количестве произведенных кораблей различными производителями.
 * При помощи RxJava
 */
@Slf4j
@RequiredArgsConstructor
public class RxBackpressureManufacturerCounterStatistics
        implements StatisticsCalculator<SpaceShipEntity, Map<String, Long>> {

    private final int bufferSize;
    private final boolean countDrops;

    @Override
    public Map<String, Long> calculate(List<SpaceShipEntity> objects) {
        ManufacturerCounterSubscriber subscriber = new ManufacturerCounterSubscriber(bufferSize);

        AtomicInteger droppedCount = new AtomicInteger(0);

        Flowable.<SpaceShipEntity>create(emitter -> {
            for (SpaceShipEntity object : objects) {
                emitter.onNext(object);
            }
            emitter.onComplete();
        }, BackpressureStrategy.BUFFER)
                .onBackpressureBuffer(
                        bufferSize >> 1,
                        () -> {},
                        BackpressureOverflowStrategy.DROP_LATEST,
                        ship -> {
                            if (countDrops) {
                                droppedCount.incrementAndGet();
                            }
                        }
                )
                .observeOn(Schedulers.computation())
                .subscribe(subscriber);

        Map<String, Long> result;
        try {
            result = subscriber.getStatisticsFuture().get(1, TimeUnit.HOURS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error("Error occurred during manufacturer statistics waiting", e);
            throw new RuntimeException(e);
        }

        if (countDrops && droppedCount.get() > 0) {
            log.info("Dropped {} ships", droppedCount.get());
        }

        return result;
    }

    @Slf4j
    @RequiredArgsConstructor
    private static class ManufacturerCounterSubscriber implements FlowableSubscriber<SpaceShipEntity> {

        private final int batchSize;
        private Subscription subscription;

        private final Map<String, Long> statistics = new HashMap<>();
        private int handledCount = 0;

        private final CompletableFuture<Map<String, Long>> statisticsFuture = new CompletableFuture<>();

        @Override
        public void onSubscribe(@NonNull Subscription s) {
            this.subscription = s;

            handledCount = 0;
            s.request(batchSize);
        }

        @Override
        public void onNext(SpaceShipEntity spaceShip) {
            String manufacturer = spaceShip.getManufacturer();
            statistics.merge(manufacturer, 1L, Long::sum);

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

        public Future<Map<String, Long>> getStatisticsFuture() {
            return statisticsFuture;
        }
    }
}
