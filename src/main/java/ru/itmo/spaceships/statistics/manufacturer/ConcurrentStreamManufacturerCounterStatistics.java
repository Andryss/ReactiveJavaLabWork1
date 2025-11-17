package ru.itmo.spaceships.statistics.manufacturer;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import ru.itmo.spaceships.model.SpaceShip;
import ru.itmo.spaceships.statistics.DelayedStatistics;
import ru.itmo.spaceships.statistics.StatisticsCalculator;

/**
 * Класс для сбора статистики о количестве произведенных кораблей различными производителями.
 * При помощи Stream API (параллельно + без задержки)
 */
public class ConcurrentStreamManufacturerCounterStatistics extends DelayedStatistics
        implements StatisticsCalculator<SpaceShip, Map<String, Long>> {

    private final int parallelism;

    public ConcurrentStreamManufacturerCounterStatistics() {
        this(0, Runtime.getRuntime().availableProcessors());
    }

    public ConcurrentStreamManufacturerCounterStatistics(long delay, int parallelism) {
        super(delay);
        this.parallelism = parallelism;
    }

    @Override
    public Map<String, Long> calculate(List<SpaceShip> objects) {
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", String.valueOf(parallelism));
        return objects.stream()
                .parallel()
                .map(ship -> ship.getManufacturer(getDelay()))
                .collect(Collectors.groupingByConcurrent(Function.identity(), Collectors.counting()));
    }
}
