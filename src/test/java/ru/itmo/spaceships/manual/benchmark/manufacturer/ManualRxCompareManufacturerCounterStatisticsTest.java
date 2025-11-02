package ru.itmo.spaceships.manual.benchmark.manufacturer;

import java.util.List;
import java.util.Map;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;
import ru.itmo.spaceships.manual.benchmark.BaseBenchmarkTest;
import ru.itmo.spaceships.manual.benchmark.config.SpaseShipsConfig;
import ru.itmo.spaceships.manual.benchmark.config.StatisticsConfig;
import ru.itmo.spaceships.model.SpaceShip;
import ru.itmo.spaceships.statistics.manufacturer.ConcurrentStreamManufacturerCounterStatistics;
import ru.itmo.spaceships.statistics.manufacturer.RxManufacturerCounterStatistics;

public class ManualRxCompareManufacturerCounterStatisticsTest extends BaseBenchmarkTest {

    @Benchmark
    public void smallRxBatchRun(
            StatisticsConfig statisticsConfig,
            SpaseShipsConfig spaseShipsConfig,
            Blackhole blackhole
    ) {
        RxManufacturerCounterStatistics statistics = statisticsConfig.getRxDelayedManufacturerCounterStatistics();
        List<SpaceShip> batch = spaseShipsConfig.getRxCompareSmall();

        Map<String, Long> result = statistics.calculate(batch);

        blackhole.consume(result);
    }

    @Benchmark
    public void mediumRxBatchRun(
            StatisticsConfig statisticsConfig,
            SpaseShipsConfig spaseShipsConfig,
            Blackhole blackhole
    ) {
        RxManufacturerCounterStatistics statistics = statisticsConfig.getRxDelayedManufacturerCounterStatistics();
        List<SpaceShip> batch = spaseShipsConfig.getRxCompareMedium();

        Map<String, Long> result = statistics.calculate(batch);

        blackhole.consume(result);
    }

    @Benchmark
    public void smallStreamBatchRun(
            StatisticsConfig statisticsConfig,
            SpaseShipsConfig spaseShipsConfig,
            Blackhole blackhole
    ) {
        ConcurrentStreamManufacturerCounterStatistics statistics =
                statisticsConfig.getConcurrentDelayedStreamManufacturerCounterStatistics();
        List<SpaceShip> batch = spaseShipsConfig.getRxCompareSmall();

        Map<String, Long> result = statistics.calculate(batch);

        blackhole.consume(result);
    }

    @Benchmark
    public void mediumStreamBatchRun(
            StatisticsConfig statisticsConfig,
            SpaseShipsConfig spaseShipsConfig,
            Blackhole blackhole
    ) {
        ConcurrentStreamManufacturerCounterStatistics statistics =
                statisticsConfig.getConcurrentDelayedStreamManufacturerCounterStatistics();
        List<SpaceShip> batch = spaseShipsConfig.getRxCompareMedium();

        Map<String, Long> result = statistics.calculate(batch);

        blackhole.consume(result);
    }

    @Override
    protected String getReportPath() {
        return "reports/benchmarks/ManufacturerCounterStatistics/rx/result-compare-stream.txt";
    }
}
