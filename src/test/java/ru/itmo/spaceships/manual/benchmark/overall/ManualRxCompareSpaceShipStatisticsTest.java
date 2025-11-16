package ru.itmo.spaceships.manual.benchmark.overall;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;
import ru.itmo.spaceships.manual.benchmark.BaseBenchmarkTest;
import ru.itmo.spaceships.manual.benchmark.config.SpaseShipsConfig;
import ru.itmo.spaceships.manual.benchmark.config.StatisticsConfig;
import ru.itmo.spaceships.model.SpaceShip;
import ru.itmo.spaceships.statistics.overall.ConcurrentStreamSpaceShipStatistics;
import ru.itmo.spaceships.statistics.overall.OverallStatistics;
import ru.itmo.spaceships.statistics.overall.RXSpaceShipStatistics;

import java.util.List;

public class ManualRxCompareSpaceShipStatisticsTest extends BaseBenchmarkTest {

    @Benchmark
    public void smallRxBatchRun(
            StatisticsConfig statisticsConfig,
            SpaseShipsConfig spaseShipsConfig,
            Blackhole blackhole
    ) {
        RXSpaceShipStatistics statistics = statisticsConfig.getRxSpaceShipStatisticsDelayed();
        List<SpaceShip> batch = spaseShipsConfig.getRxCompareSmall();

        OverallStatistics result = statistics.calculate(batch);

        blackhole.consume(result);
    }

    @Benchmark
    public void mediumRxBatchRun(
            StatisticsConfig statisticsConfig,
            SpaseShipsConfig spaseShipsConfig,
            Blackhole blackhole
    ) {
        RXSpaceShipStatistics statistics = statisticsConfig.getRxSpaceShipStatisticsDelayed();
        List<SpaceShip> batch = spaseShipsConfig.getRxCompareMedium();

        OverallStatistics result = statistics.calculate(batch);

        blackhole.consume(result);
    }

    @Benchmark
    public void smallStreamBatchRun(
            StatisticsConfig statisticsConfig,
            SpaseShipsConfig spaseShipsConfig,
            Blackhole blackhole
    ) {
        ConcurrentStreamSpaceShipStatistics statistics =
                statisticsConfig.getConcurrentDelayedStreamSpaceShipStatistics();
        List<SpaceShip> batch = spaseShipsConfig.getRxCompareSmall();

        OverallStatistics result = statistics.calculate(batch);

        blackhole.consume(result);
    }

    @Benchmark
    public void mediumStreamBatchRun(
            StatisticsConfig statisticsConfig,
            SpaseShipsConfig spaseShipsConfig,
            Blackhole blackhole
    ) {
        ConcurrentStreamSpaceShipStatistics statistics =
                statisticsConfig.getConcurrentDelayedStreamSpaceShipStatistics();
        List<SpaceShip> batch = spaseShipsConfig.getRxCompareMedium();

        OverallStatistics result = statistics.calculate(batch);

        blackhole.consume(result);
    }

    @Override
    protected String getReportPath() {
        return "reports/benchmarks/Overall/rx/result-compare-stream.txt";
    }
}
