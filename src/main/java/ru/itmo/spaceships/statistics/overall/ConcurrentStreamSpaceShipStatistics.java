package ru.itmo.spaceships.statistics.overall;

import ru.itmo.spaceships.model.SpaceShip;
import ru.itmo.spaceships.statistics.DelayedStatistics;
import ru.itmo.spaceships.statistics.StatisticsCalculator;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.itmo.spaceships.statistics.overall.OverallStatistics.DATE_FORMATTER;

public class ConcurrentStreamSpaceShipStatistics extends DelayedStatistics
        implements StatisticsCalculator<SpaceShip, OverallStatistics> {
    public ConcurrentStreamSpaceShipStatistics(long delay) {
        super(delay);
    }

    public ConcurrentStreamSpaceShipStatistics() {
        super();
    }
    @Override
    public OverallStatistics calculate(List<SpaceShip> objects) {
        OverallStatistics accumulator = new OverallStatistics();

        accumulator.setCountByManufacturer(objects.stream().parallel()
                .collect(Collectors.groupingByConcurrent(s -> s.getManufacturer(getDelay()), Collectors.counting())));

        accumulator.setCountByFuelType(objects.stream().parallel()
                .collect(Collectors.groupingByConcurrent(v ->
                        v.getEngine().getFuelType().name(), Collectors.counting())));

        accumulator.setCountByDate(objects.stream().parallel()
                .map(ship -> DATE_FORMATTER.format(ship.getManufactureDate()))
                .collect(Collectors.groupingByConcurrent(Function.identity(), Collectors.counting())));

        accumulator.getAggregateMaxSpeed().combine(
                objects.stream().parallel().mapToLong(SpaceShip::getMaxSpeed).summaryStatistics()
        );

        accumulator.getAggregateCrewMembers().combine(
                objects.stream().parallel().mapToLong(v -> v.getCrew().size()).summaryStatistics()
        );

        accumulator.getAggregateLength().combine(
                objects.stream().parallel().mapToLong(v -> v.getDimensions().length()).summaryStatistics()
        );

        accumulator.setAggregateCrewByShipType(objects.stream().parallel()
                .collect(Collectors.groupingByConcurrent(
                        ship -> ship.getType().name(),
                        Collectors.summarizingLong(ship -> ship.getCrew().size())
                )));

        return accumulator;
    }
}
