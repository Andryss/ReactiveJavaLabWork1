package ru.itmo.spaceships.statistics;

import ru.itmo.spaceships.model.SpaceShip;

import java.util.List;
import java.util.stream.Collectors;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class StreamSpaceShipStatistics implements StatisticsCalculator<SpaceShip, StatisticsAccumulator> {

	@Override
	public StatisticsAccumulator calculate(List<SpaceShip> objects) {
		StatisticsAccumulator accumulator = new StatisticsAccumulator();

		accumulator.getCountByManufacturer().accumulator = objects.stream()
				.collect(Collectors.groupingBy(SpaceShip::getManufacturer, Collectors.counting()));

		accumulator.getCountByFuelType().accumulator = objects.stream()
				.collect(Collectors.groupingBy(v -> v.getEngine().getFuelType().name(), Collectors.counting()));

		accumulator.getCountByDate().accumulator = objects.stream()
				.collect(Collectors.groupingBy(v -> DateTimeFormatter.ofPattern("dd.MM.yyyy").withZone(ZoneId.systemDefault()).format(v.getManufactureDate()), Collectors.counting()));

		accumulator.getAggregateMaxSpeed().combine(objects.stream().mapToLong(SpaceShip::getMaxSpeed).summaryStatistics());

		accumulator.getAggregateCrewMembers().combine(objects.stream().mapToLong(v -> v.getCrew().size()).summaryStatistics());

		accumulator.getAggregateLength().combine(objects.stream().mapToLong(v -> v.getDimensions().length()).summaryStatistics());

		accumulator.getAggregateCrewByShipType().accumulator = objects.stream()
				.collect(Collectors.groupingBy(
						ship -> ship.getType().name(),
						Collectors.summarizingLong(ship -> ship.getCrew().size())
				));

		return accumulator;
	}
}
