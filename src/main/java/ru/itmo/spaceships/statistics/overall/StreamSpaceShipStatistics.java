package ru.itmo.spaceships.statistics.overall;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import ru.itmo.spaceships.model.SpaceShip;
import ru.itmo.spaceships.statistics.StatisticsCalculator;

import static ru.itmo.spaceships.statistics.overall.OverallStatistics.DATE_FORMATTER;

public class StreamSpaceShipStatistics implements StatisticsCalculator<SpaceShip, OverallStatistics> {

    @Override
    public OverallStatistics calculate(List<SpaceShip> objects) {
        OverallStatistics accumulator = new OverallStatistics();

        accumulator.setCountByManufacturer(objects.stream()
                .collect(Collectors.groupingBy(SpaceShip::getManufacturer, Collectors.counting())));

        accumulator.setCountByFuelType(objects.stream()
                .collect(Collectors.groupingBy(v -> v.getEngine().getFuelType().name(), Collectors.counting())));

        accumulator.setCountByDate(objects.stream()
                .map(ship -> DATE_FORMATTER.format(ship.getManufactureDate()))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting())));

        accumulator.getAggregateMaxSpeed().combine(
                objects.stream().mapToLong(SpaceShip::getMaxSpeed).summaryStatistics()
        );

        accumulator.getAggregateCrewMembers().combine(
                objects.stream().mapToLong(v -> v.getCrew().size()).summaryStatistics()
        );

        accumulator.getAggregateLength().combine(
                objects.stream().mapToLong(v -> v.getDimensions().length()).summaryStatistics()
        );

        accumulator.setAggregateCrewByShipType(objects.stream()
                .collect(Collectors.groupingBy(
                        ship -> ship.getType().name(),
                        Collectors.summarizingLong(ship -> ship.getCrew().size())
                )));

        return accumulator;
    }
}
