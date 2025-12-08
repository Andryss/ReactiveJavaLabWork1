package ru.itmo.spaceships.statistics.manufacturer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import ru.itmo.spaceships.model.SpaceShipEntity;
import ru.itmo.spaceships.statistics.StatisticsCalculator;

/**
 * Класс для сбора статистики о количестве произведенных кораблей различными производителями.
 * При помощи собственного Collector
 */
public class CollectorManufacturerCounterStatistics
        implements StatisticsCalculator<SpaceShipEntity, Map<String, Long>> {

    @Override
    public Map<String, Long> calculate(List<SpaceShipEntity> objects) {
        return objects.stream()
                .parallel()
                .collect(new ManufacturerCounterCollector());
    }

    private static class ManufacturerCounterCollector
            implements Collector<SpaceShipEntity, Map<String, AtomicLong>, Map<String, Long>> {

        @Override
        public Supplier<Map<String, AtomicLong>> supplier() {
            return ConcurrentHashMap::new;
        }

        @Override
        public BiConsumer<Map<String, AtomicLong>, SpaceShipEntity> accumulator() {
            return (result, spaceShip) ->
                    result.computeIfAbsent(spaceShip.getManufacturer(), key -> new AtomicLong(0))
                            .incrementAndGet();
        }

        @Override
        public BinaryOperator<Map<String, AtomicLong>> combiner() {
            return (leftCounts, rightCounts) -> {
                rightCounts.forEach((manufacturer, count) ->
                        leftCounts.computeIfAbsent(manufacturer, key -> new AtomicLong(0))
                                .addAndGet(count.get()));
                return leftCounts;
            };
        }

        @Override
        public Function<Map<String, AtomicLong>, Map<String, Long>> finisher() {
            return computed -> {
                HashMap<String, Long> result = new HashMap<>();
                computed.forEach((manufacturer, count) -> result.put(manufacturer, count.get()));
                return result;
            };
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Set.of(Characteristics.CONCURRENT, Characteristics.UNORDERED);
        }
    }
}
