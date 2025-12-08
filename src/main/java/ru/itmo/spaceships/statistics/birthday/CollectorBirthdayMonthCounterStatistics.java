package ru.itmo.spaceships.statistics.birthday;

import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import ru.itmo.spaceships.model.CrewMember;
import ru.itmo.spaceships.model.SpaceShipEntity;
import ru.itmo.spaceships.statistics.StatisticsCalculator;

/**
 * Класс для сбора статистики о количестве членов экипажа, рожденных в разные месяцы.
 * При помощи собственного Collector
 */
public class CollectorBirthdayMonthCounterStatistics implements StatisticsCalculator<SpaceShipEntity, Map<Month, Long>> {

    @Override
    public Map<Month, Long> calculate(List<SpaceShipEntity> objects) {
        return objects.stream()
                .parallel()
                .collect(new BirthdayMonthCounterCollector());
    }

    private static class BirthdayMonthCounterCollector
            implements Collector<SpaceShipEntity, Map<Month, Long>, Map<Month, Long>> {

        @Override
        public Supplier<Map<Month, Long>> supplier() {
            return ConcurrentHashMap::new;
        }

        @Override
        public BiConsumer<Map<Month, Long>, SpaceShipEntity> accumulator() {
            return (result, spaseShip) -> {
                for (CrewMember member : spaseShip.getCrew()) {
                    result.merge(member.getBirthDate().getMonth(), 1L, Long::sum);
                }
            };
        }

        @Override
        public BinaryOperator<Map<Month, Long>> combiner() {
            return (countLeft, countRight) -> {
                countRight.forEach((month, count) -> countLeft.merge(month, count, Long::sum));
                return countLeft;
            };
        }

        @Override
        public Function<Map<Month, Long>, Map<Month, Long>> finisher() {
            return Function.identity();
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Set.of(Characteristics.CONCURRENT, Characteristics.UNORDERED, Characteristics.IDENTITY_FINISH);
        }
    }
}
