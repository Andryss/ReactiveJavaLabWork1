package ru.itmo.spaceships.statistics.overall;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ru.itmo.spaceships.model.SpaceShip;
import ru.itmo.spaceships.statistics.DelayedStatistics;
import ru.itmo.spaceships.statistics.StatisticsCalculator;

import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.itmo.spaceships.statistics.overall.OverallStatistics.DATE_FORMATTER;

public class RXSpaceShipStatistics extends DelayedStatistics
		implements StatisticsCalculator<SpaceShip, OverallStatistics> {
	public RXSpaceShipStatistics(long delay) {
		super(delay);
	}

	public RXSpaceShipStatistics() {
		super();
	}

	@Override
	public OverallStatistics calculate(List<SpaceShip> objects) {
		Observable<SpaceShip> shipsObservable = Observable.fromIterable(objects).cache();
		OverallStatistics result = new OverallStatistics();

		result.setCountByManufacturer(shipsObservable
				.subscribeOn(Schedulers.computation())
				.flatMap(s -> Observable.fromCallable(() -> s.getManufacturer(getDelay())).subscribeOn(Schedulers.io())
				)
				.collect(Collectors.groupingByConcurrent(Function.identity(), Collectors.counting()))
				.blockingGet());

		result.setCountByDate(shipsObservable
				.subscribeOn(Schedulers.computation())
				.map(ship -> DATE_FORMATTER.format(ship.getManufactureDate()))
				.collect(Collectors.groupingByConcurrent(Function.identity(), Collectors.counting()))
				.blockingGet());

		result.setCountByFuelType(shipsObservable
				.subscribeOn(Schedulers.computation())
				.map(ship -> ship.getEngine().getFuelType().name())
				.collect(Collectors.groupingByConcurrent(Function.identity(), Collectors.counting()))
				.blockingGet());

		result.setAggregateMaxSpeed(shipsObservable
				.subscribeOn(Schedulers.computation())
				.map(SpaceShip::getMaxSpeed)
				.map(Integer::longValue)
				.collect(LongSummaryStatistics::new, LongSummaryStatistics::accept)
				.blockingGet());

		result.setAggregateCrewMembers(shipsObservable
				.subscribeOn(Schedulers.computation())
				.map(v -> v.getCrew().size())
				.map(Integer::longValue)
				.collect(LongSummaryStatistics::new, LongSummaryStatistics::accept)
				.blockingGet());

		result.setAggregateLength(shipsObservable
				.subscribeOn(Schedulers.computation())
				.map(v -> v.getDimensions().length())
				.collect(LongSummaryStatistics::new, LongSummaryStatistics::accept)
				.blockingGet());

		result.setAggregateCrewByShipType(shipsObservable
				.subscribeOn(Schedulers.computation())
				.collect(Collectors.groupingByConcurrent(
						ship -> ship.getType().name(),
						Collectors.summarizingLong(ship -> ship.getCrew().size())
				))
				.blockingGet());

		return result;
	}
}
