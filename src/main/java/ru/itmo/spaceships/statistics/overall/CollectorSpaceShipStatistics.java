package ru.itmo.spaceships.statistics.overall;

import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import ru.itmo.spaceships.model.SpaceShip;
import ru.itmo.spaceships.statistics.StatisticsCalculator;

import static ru.itmo.spaceships.statistics.overall.OverallStatistics.DATE_FORMATTER;

public class CollectorSpaceShipStatistics implements StatisticsCalculator<SpaceShip, OverallStatistics> {

    @Override
    public OverallStatistics calculate(List<SpaceShip> objects) {
        return objects.stream()
                .collect(new SpaceShipStatisticsCollector());
    }

    private static class SpaceShipStatisticsCollector
            implements Collector<SpaceShip, OverallStatistics, OverallStatistics> {

        @Override
        public Supplier<OverallStatistics> supplier() {
            return OverallStatistics::new;
        }

        @Override
        public BiConsumer<OverallStatistics, SpaceShip> accumulator() {
            return (acc, ship) -> {
                acc.getCountByManufacturer().merge(ship.getManufacturer(), 1L, Long::sum);

                acc.getCountByFuelType().merge(ship.getEngine().getFuelType().name(), 1L, Long::sum);

                acc.getCountByDate().merge(DATE_FORMATTER.format(ship.getManufactureDate()), 1L, Long::sum);

                acc.getAggregateMaxSpeed().accept(ship.getMaxSpeed());
                acc.getAggregateCrewMembers().accept(ship.getCrew().size());
                acc.getAggregateLength().accept(ship.getDimensions().length());

                acc.getAggregateCrewByShipType().merge(ship.getType().name(),
                        new LongSummaryStatistics(),
                        (oldStats, newStats) -> {
                            oldStats.accept(ship.getCrew().size());
                            return oldStats;
                        });
            };
        }

        @Override
        public BinaryOperator<OverallStatistics> combiner() {
            return (acc1, acc2) -> {
                mergeMaps(acc1.getCountByManufacturer(), acc2.getCountByManufacturer());

                mergeMaps(acc1.getCountByFuelType(), acc2.getCountByFuelType());

                mergeMaps(acc1.getCountByDate(), acc2.getCountByDate());

                acc1.getAggregateMaxSpeed().combine(acc2.getAggregateMaxSpeed());
                acc1.getAggregateCrewMembers().combine(acc2.getAggregateCrewMembers());
                acc1.getAggregateLength().combine(acc2.getAggregateLength());

                mergeSummaryStatisticsMaps(acc1.getAggregateCrewByShipType(),
                        acc2.getAggregateCrewByShipType());

                return acc1;
            };
        }

        @Override
        public Function<OverallStatistics, OverallStatistics> finisher() {
            return Function.identity();
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Set.of(Characteristics.UNORDERED, Characteristics.IDENTITY_FINISH);
        }

        private <K> void mergeMaps(Map<K, Long> map1, Map<K, Long> map2) {
            map2.forEach((key, value) -> map1.merge(key, value, Long::sum));
        }

        private void mergeSummaryStatisticsMaps(Map<String, LongSummaryStatistics> map1,
                                                Map<String, LongSummaryStatistics> map2) {
            map2.forEach((key, stats) -> map1.merge(key, stats, (oldStats, newStats) -> {
                oldStats.combine(newStats);
                return oldStats;
            }));
        }
    }
}

