package ru.itmo.spaceships.manual.benchmark.manufacturer;

import java.util.List;
import java.util.Map;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;
import ru.itmo.spaceships.manual.benchmark.BaseBenchmarkTest;
import ru.itmo.spaceships.manual.benchmark.config.SpaseShipsConfig;
import ru.itmo.spaceships.manual.benchmark.config.StatisticsConfig;
import ru.itmo.spaceships.model.SpaceShip;
import ru.itmo.spaceships.statistics.manufacturer.ConcurrentDelayedStreamManufacturerCounterStatistics;
import ru.itmo.spaceships.statistics.manufacturer.ConcurrentStreamManufacturerCounterStatistics;
import ru.itmo.spaceships.statistics.manufacturer.DelayedStreamManufacturerCounterStatistics;
import ru.itmo.spaceships.statistics.manufacturer.StreamManufacturerCounterStatistics;

public class ManualSameTimeStreamManufacturerCounterStatisticsTest extends BaseBenchmarkTest {

    @Benchmark
    public void sequenceWithoutDelayBatchRun(
            StatisticsConfig statisticsConfig,
            SpaseShipsConfig spaseShipsConfig,
            Blackhole blackhole
    ) {
        StreamManufacturerCounterStatistics statistics = statisticsConfig.getStreamManufacturerCounterStatistics();
        List<SpaceShip> batch = spaseShipsConfig.getWithoutDelayBatch();

        Map<String, Long> result = statistics.calculate(batch);

        blackhole.consume(result);
    }

    @Benchmark
    public void parallelWithoutDelayBatchRun(
            StatisticsConfig statisticsConfig,
            SpaseShipsConfig spaseShipsConfig,
            Blackhole blackhole
    ) {
        ConcurrentStreamManufacturerCounterStatistics statistics =
                statisticsConfig.getConcurrentStreamManufacturerCounterStatistics();
        List<SpaceShip> batch = spaseShipsConfig.getWithoutDelayBatch();

        Map<String, Long> result = statistics.calculate(batch);

        blackhole.consume(result);
    }

    @Benchmark
    public void sequenceWithDelayBatchRun(
            StatisticsConfig statisticsConfig,
            SpaseShipsConfig spaseShipsConfig,
            Blackhole blackhole
    ) {
        DelayedStreamManufacturerCounterStatistics statistics =
                statisticsConfig.getDelayedStreamManufacturerCounterStatistics();
        List<SpaceShip> batch = spaseShipsConfig.getWithDelayBatch();

        Map<String, Long> result = statistics.calculate(batch);

        blackhole.consume(result);
    }

    @Benchmark
    public void parallelWithDelayBatchRun(
            StatisticsConfig statisticsConfig,
            SpaseShipsConfig spaseShipsConfig,
            Blackhole blackhole
    ) {
        ConcurrentDelayedStreamManufacturerCounterStatistics statistics =
                statisticsConfig.getConcurrentDelayedStreamManufacturerCounterStatistics();
        List<SpaceShip> batch = spaseShipsConfig.getWithDelayBatch();

        Map<String, Long> result = statistics.calculate(batch);

        blackhole.consume(result);
    }

    @Override
    protected String getReportPath() {
        return "reports/benchmarks/ManufacturerCounterStatistics/stream/result-same-time.txt";
    }
}
