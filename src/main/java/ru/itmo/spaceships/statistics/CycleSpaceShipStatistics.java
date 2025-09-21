package ru.itmo.spaceships.statistics;

import ru.itmo.spaceships.model.SpaceShip;
import ru.itmo.spaceships.statistics.accumulator.AggregateAccumulator;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import java.util.List;

public class CycleSpaceShipStatistics implements StatisticsCalculator<SpaceShip, StatisticsAccumulator> {

	@Override
	public StatisticsAccumulator calculate(List<SpaceShip> objects) {
		StatisticsAccumulator accumulator = new StatisticsAccumulator();
		for (SpaceShip object : objects) {
			accumulator.getCountByManufacturer().accumulator.merge(object.getManufacturer(), 1L, Long::sum);
			accumulator.getCountByFuelType().accumulator.merge(
					object.getEngine().getFuelType().name(),
					1L,
					Long::sum
			);
			accumulator.getCountByDate().accumulator.merge(
					DateTimeFormatter
							.ofPattern("dd.MM.yyyy")
							.withZone(ZoneId.systemDefault())
							.format(object.getManufactureDate()),
					1L,
					Long::sum
			);
			accumulator.getAggregateMaxSpeed().accept(object.getMaxSpeed());
			accumulator.getAggregateCrewMembers().accept(object.getCrew().size());
			accumulator.getAggregateLength().accept(object.getDimensions().length());
			accumulator.getAggregateCrewByShipType().accumulator.compute(object.getType().name(), (k, v) -> {
				if (v == null) {
					v = new AggregateAccumulator();
				}
				v.accept(object.getCrew().size());
				return v;
			});
		}
		return accumulator;
	}
}
