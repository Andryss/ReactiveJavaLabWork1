package ru.itmo.spaceships.statistics.overall;

import ru.itmo.spaceships.model.SpaceShip;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.itmo.spaceships.statistics.overall.OverallStatistics.DATE_FORMATTER;

public class SequenceStreamSpaceShipStatistics extends StreamSpaceShipStatistics {

    public SequenceStreamSpaceShipStatistics(long delay) {
        super(delay);
    }

    public SequenceStreamSpaceShipStatistics() {
        super();
    }

    @Override
    public OverallStatistics calculate(List<SpaceShip> objects) {
        OverallStatistics accumulator = new OverallStatistics();

        accumulator.setCountByManufacturer(objects.stream()
                .collect(Collectors.groupingByConcurrent(s -> s.getManufacturer(delay), Collectors.counting())));

        accumulator.setCountByFuelType(objects.stream()
                .collect(Collectors.groupingByConcurrent(v -> v.getEngine().getFuelType().name(), Collectors.counting())));

        accumulator.setCountByDate(objects.stream()
                .map(ship -> DATE_FORMATTER.format(ship.getManufactureDate()))
                .collect(Collectors.groupingByConcurrent(Function.identity(), Collectors.counting())));

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
                .collect(Collectors.groupingByConcurrent(
                        ship -> ship.getType().name(),
                        Collectors.summarizingLong(ship -> ship.getCrew().size())
                )));

        return accumulator;
    }
}
