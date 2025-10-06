package ru.itmo.spaceships.statistics.overall;

import ru.itmo.spaceships.model.SpaceShip;
import ru.itmo.spaceships.statistics.SpaceShipSpliterator;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static ru.itmo.spaceships.statistics.overall.OverallStatistics.DATE_FORMATTER;

public class SpliteratorConcurrentStreamSpaceShipStatistics extends StreamSpaceShipStatistics {

    @Override
    public OverallStatistics calculate(List<SpaceShip> objects) {
        OverallStatistics accumulator = new OverallStatistics();

        accumulator.setCountByManufacturer(StreamSupport.stream(new SpaceShipSpliterator(objects), true)
                .collect(Collectors.groupingByConcurrent(s -> s.getManufacturer(delay), Collectors.counting())));

        accumulator.setCountByFuelType(StreamSupport.stream(new SpaceShipSpliterator(objects), true)
                .collect(Collectors.groupingByConcurrent(v -> v.getEngine().getFuelType().name(),
                        Collectors.counting())));

        accumulator.setCountByDate(StreamSupport.stream(new SpaceShipSpliterator(objects), true)
                .map(ship -> DATE_FORMATTER.format(ship.getManufactureDate()))
                .collect(Collectors.groupingByConcurrent(Function.identity(), Collectors.counting())));

        accumulator.getAggregateMaxSpeed().combine(
                StreamSupport.stream(new SpaceShipSpliterator(objects), true)
                        .mapToLong(SpaceShip::getMaxSpeed)
                        .summaryStatistics()
        );

        accumulator.getAggregateCrewMembers().combine(
                StreamSupport.stream(new SpaceShipSpliterator(objects), true)
                        .mapToLong(v -> v.getCrew().size()).summaryStatistics()
        );

        accumulator.getAggregateLength().combine(
                StreamSupport.stream(new SpaceShipSpliterator(objects), true)
                        .mapToLong(v -> v.getDimensions().length())
                        .summaryStatistics()
        );

        accumulator.setAggregateCrewByShipType(StreamSupport.stream(new SpaceShipSpliterator(objects), true)
                .collect(Collectors.groupingByConcurrent(
                        ship -> ship.getType().name(),
                        Collectors.summarizingLong(ship -> ship.getCrew().size())
                )));

        return accumulator;
    }
}
