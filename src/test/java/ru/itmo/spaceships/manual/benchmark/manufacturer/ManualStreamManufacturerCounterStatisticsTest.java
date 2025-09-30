package ru.itmo.spaceships.manual.benchmark.manufacturer;

import java.util.List;
import java.util.Map;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;
import ru.itmo.spaceships.manual.benchmark.BaseBenchmarkTest;
import ru.itmo.spaceships.manual.benchmark.config.SpaseShipsConfig;
import ru.itmo.spaceships.manual.benchmark.config.StatisticsConfig;
import ru.itmo.spaceships.model.SpaceShip;
import ru.itmo.spaceships.statistics.manufacturer.StreamManufacturerCounterStatistics;

public class ManualStreamManufacturerCounterStatisticsTest extends BaseBenchmarkTest {

    @Benchmark
    public void milliBatchRun(
            StatisticsConfig statisticsConfig,
            SpaseShipsConfig spaseShipsConfig,
            Blackhole blackhole
    ) {
        StreamManufacturerCounterStatistics statistics = statisticsConfig.getStreamManufacturerCounterStatistics();
        List<SpaceShip> batch = spaseShipsConfig.getMilliBatch();

        Map<String, Long> result = statistics.calculate(batch);

        blackhole.consume(result);
    }

    @Benchmark
    public void smallBatchRun(
            StatisticsConfig statisticsConfig,
            SpaseShipsConfig spaseShipsConfig,
            Blackhole blackhole
    ) {
        StreamManufacturerCounterStatistics statistics = statisticsConfig.getStreamManufacturerCounterStatistics();
        List<SpaceShip> batch = spaseShipsConfig.getSmallBatch();

        Map<String, Long> result = statistics.calculate(batch);

        blackhole.consume(result);
    }

    @Benchmark
    public void mediumBatchRun(
            StatisticsConfig statisticsConfig,
            SpaseShipsConfig spaseShipsConfig,
            Blackhole blackhole
    ) {
        StreamManufacturerCounterStatistics statistics = statisticsConfig.getStreamManufacturerCounterStatistics();
        List<SpaceShip> batch = spaseShipsConfig.getMediumBatch();

        Map<String, Long> result = statistics.calculate(batch);

        blackhole.consume(result);
    }

    @Benchmark
    public void largeBatchRun(
            StatisticsConfig statisticsConfig,
            SpaseShipsConfig spaseShipsConfig,
            Blackhole blackhole
    ) {
        StreamManufacturerCounterStatistics statistics = statisticsConfig.getStreamManufacturerCounterStatistics();
        List<SpaceShip> batch = spaseShipsConfig.getLargeBatch();

        Map<String, Long> result = statistics.calculate(batch);

        blackhole.consume(result);
    }

    @Override
    protected String getReportPath() {
        return "reports/benchmarks/ManufacturerCounterStatistics/stream/result.txt";
    }
}
