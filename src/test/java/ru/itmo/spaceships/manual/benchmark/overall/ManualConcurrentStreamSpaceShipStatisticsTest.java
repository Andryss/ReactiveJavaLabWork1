package ru.itmo.spaceships.manual.benchmark.overall;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;
import ru.itmo.spaceships.manual.benchmark.BaseBenchmarkTest;
import ru.itmo.spaceships.manual.benchmark.config.SpaseShipsConfig;
import ru.itmo.spaceships.manual.benchmark.config.StatisticsConfig;
import ru.itmo.spaceships.model.SpaceShip;
import ru.itmo.spaceships.statistics.overall.ConcurrentStreamSpaceShipStatistics;
import ru.itmo.spaceships.statistics.overall.OverallStatistics;

import java.util.List;

public class ManualConcurrentStreamSpaceShipStatisticsTest extends BaseBenchmarkTest {
    @Benchmark
    public void milliBatchRun(
            StatisticsConfig statisticsConfig,
            SpaseShipsConfig spaseShipsConfig,
            Blackhole blackhole
    ) {
        ConcurrentStreamSpaceShipStatistics statistics = statisticsConfig.getConcurrentStreamSpaceShipStatistics();
        List<SpaceShip> batch = spaseShipsConfig.getMilliBatch();

        OverallStatistics result = statistics.calculate(batch);

        blackhole.consume(result);
    }

    @Benchmark
    public void milliBatchRunWithDelay(
            StatisticsConfig statisticsConfig,
            SpaseShipsConfig spaseShipsConfig,
            Blackhole blackhole
    ) {
        ConcurrentStreamSpaceShipStatistics statistics = statisticsConfig.getConcurrentDelayedStreamSpaceShipStatistics();
        List<SpaceShip> batch = spaseShipsConfig.getMilliBatch();

        OverallStatistics result = statistics.calculate(batch);

        blackhole.consume(result);
    }

    @Benchmark
    public void smallBatchRun(
            StatisticsConfig statisticsConfig,
            SpaseShipsConfig spaseShipsConfig,
            Blackhole blackhole
    ) {
        ConcurrentStreamSpaceShipStatistics statistics = statisticsConfig.getConcurrentStreamSpaceShipStatistics();
        List<SpaceShip> batch = spaseShipsConfig.getSmallBatch();

        OverallStatistics result = statistics.calculate(batch);

        blackhole.consume(result);
    }

    @Benchmark
    public void smallBatchRunWithDelay(
            StatisticsConfig statisticsConfig,
            SpaseShipsConfig spaseShipsConfig,
            Blackhole blackhole
    ) {
        ConcurrentStreamSpaceShipStatistics statistics = statisticsConfig.getConcurrentDelayedStreamSpaceShipStatistics();
        List<SpaceShip> batch = spaseShipsConfig.getSmallBatch();

        OverallStatistics result = statistics.calculate(batch);

        blackhole.consume(result);
    }

    @Override
    protected String getReportPath() {
        return "reports/benchmarks/Overall/stream/result-concurrent.txt";
    }
}
