package ru.itmo.spaceships.statistics.overall;

import java.util.List;
import java.util.LongSummaryStatistics;

import ru.itmo.spaceships.model.SpaceShipEntity;
import ru.itmo.spaceships.statistics.StatisticsCalculator;

import static ru.itmo.spaceships.statistics.overall.OverallStatistics.DATE_FORMATTER;

public class CycleSpaceShipStatistics implements StatisticsCalculator<SpaceShipEntity, OverallStatistics> {

    @Override
    public OverallStatistics calculate(List<SpaceShipEntity> objects) {
        OverallStatistics accumulator = new OverallStatistics();
        for (SpaceShipEntity object : objects) {
            accumulator.getCountByManufacturer().merge(object.getManufacturer(), 1L, Long::sum);
            accumulator.getCountByFuelType().merge(
                    object.getEngine().getFuelType().name(),
                    1L,
                    Long::sum
            );
            accumulator.getCountByDate().merge(
                    DATE_FORMATTER.format(object.getManufactureDate()),
                    1L,
                    Long::sum
            );
            accumulator.getAggregateMaxSpeed().accept(object.getMaxSpeed());
            accumulator.getAggregateCrewMembers().accept(object.getCrew().size());
            accumulator.getAggregateLength().accept(object.getDimensions().length());
            accumulator.getAggregateCrewByShipType().compute(object.getType().name(), (k, v) -> {
                if (v == null) {
                    v = new LongSummaryStatistics();
                }
                v.accept(object.getCrew().size());
                return v;
            });
        }
        return accumulator;
    }
}
