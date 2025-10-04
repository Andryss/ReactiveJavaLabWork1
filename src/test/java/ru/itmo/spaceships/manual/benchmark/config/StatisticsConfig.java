package ru.itmo.spaceships.manual.benchmark.config;

import lombok.Getter;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import ru.itmo.spaceships.statistics.manufacturer.ConcurrentDelayedStreamManufacturerCounterStatistics;
import ru.itmo.spaceships.statistics.manufacturer.ConcurrentStreamManufacturerCounterStatistics;
import ru.itmo.spaceships.statistics.manufacturer.CycleManufacturerCounterStatistics;
import ru.itmo.spaceships.statistics.manufacturer.DelayedStreamManufacturerCounterStatistics;
import ru.itmo.spaceships.statistics.manufacturer.SpliteratorDelayedStreamManufacturerCounterStatistics;
import ru.itmo.spaceships.statistics.manufacturer.SpliteratorStreamManufacturerCounterStatistics;
import ru.itmo.spaceships.statistics.manufacturer.StreamManufacturerCounterStatistics;

/**
 * Состояние со всеми статистиками.
 * Вынесено в STATE-класс, чтобы не тратить время на инициализацию во время замеров
 */
@Getter
@State(Scope.Benchmark)
public class StatisticsConfig {
    private final CycleManufacturerCounterStatistics cycleManufacturerCounterStatistics =
            new CycleManufacturerCounterStatistics();

    private final StreamManufacturerCounterStatistics streamManufacturerCounterStatistics =
            new StreamManufacturerCounterStatistics();
    private final DelayedStreamManufacturerCounterStatistics delayedStreamManufacturerCounterStatistics =
            new DelayedStreamManufacturerCounterStatistics();

    private final ConcurrentStreamManufacturerCounterStatistics concurrentStreamManufacturerCounterStatistics =
            new ConcurrentStreamManufacturerCounterStatistics();
    private final ConcurrentDelayedStreamManufacturerCounterStatistics concurrentDelayedStreamManufacturerCounterStatistics =
            new ConcurrentDelayedStreamManufacturerCounterStatistics();

    private final SpliteratorStreamManufacturerCounterStatistics spliteratorStreamManufacturerCounterStatistics =
            new SpliteratorStreamManufacturerCounterStatistics();
    private final SpliteratorDelayedStreamManufacturerCounterStatistics spliteratorDelayedStreamManufacturerCounterStatistics =
            new SpliteratorDelayedStreamManufacturerCounterStatistics();
}
