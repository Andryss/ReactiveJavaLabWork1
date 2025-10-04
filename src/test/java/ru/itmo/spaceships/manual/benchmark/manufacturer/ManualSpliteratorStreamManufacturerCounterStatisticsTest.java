package ru.itmo.spaceships.manual.benchmark.manufacturer;

import java.util.List;
import java.util.Map;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;
import ru.itmo.spaceships.manual.benchmark.BaseBenchmarkTest;
import ru.itmo.spaceships.manual.benchmark.config.SpaseShipsConfig;
import ru.itmo.spaceships.manual.benchmark.config.StatisticsConfig;
import ru.itmo.spaceships.model.SpaceShip;
import ru.itmo.spaceships.statistics.manufacturer.SpliteratorDelayedStreamManufacturerCounterStatistics;
import ru.itmo.spaceships.statistics.manufacturer.SpliteratorStreamManufacturerCounterStatistics;

public class ManualSpliteratorStreamManufacturerCounterStatisticsTest extends BaseBenchmarkTest {

    @Benchmark
    public void parallelWithoutDelayBatchRun(
            StatisticsConfig statisticsConfig,
            SpaseShipsConfig spaseShipsConfig,
            Blackhole blackhole
    ) {
        SpliteratorStreamManufacturerCounterStatistics statistics =
                statisticsConfig.getSpliteratorStreamManufacturerCounterStatistics();
        List<SpaceShip> batch = spaseShipsConfig.getWithoutDelayBatch();

        Map<String, Long> result = statistics.calculate(batch);

        blackhole.consume(result);
    }

    @Benchmark
    public void parallelWithDelayBatchRun(
            StatisticsConfig statisticsConfig,
            SpaseShipsConfig spaseShipsConfig,
            Blackhole blackhole
    ) {
        SpliteratorDelayedStreamManufacturerCounterStatistics statistics =
                statisticsConfig.getSpliteratorDelayedStreamManufacturerCounterStatistics();
        List<SpaceShip> batch = spaseShipsConfig.getWithDelayBatch();

        Map<String, Long> result = statistics.calculate(batch);

        blackhole.consume(result);
    }

    @Override
    protected String getReportPath() {
        return "reports/benchmarks/ManufacturerCounterStatistics/stream/result-spliterator.txt";
    }
}
