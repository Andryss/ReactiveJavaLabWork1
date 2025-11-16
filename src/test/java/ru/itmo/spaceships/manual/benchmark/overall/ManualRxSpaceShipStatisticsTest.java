package ru.itmo.spaceships.manual.benchmark.overall;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;
import ru.itmo.spaceships.manual.benchmark.BaseBenchmarkTest;
import ru.itmo.spaceships.manual.benchmark.config.SpaseShipsConfig;
import ru.itmo.spaceships.manual.benchmark.config.StatisticsConfig;
import ru.itmo.spaceships.model.SpaceShip;
import ru.itmo.spaceships.statistics.overall.OverallStatistics;
import ru.itmo.spaceships.statistics.overall.RXSpaceShipStatistics;

import java.util.List;
public class ManualRxSpaceShipStatisticsTest extends BaseBenchmarkTest {

    @Benchmark
    public void smallBatchRun(
            StatisticsConfig statisticsConfig,
            SpaseShipsConfig spaseShipsConfig,
            Blackhole blackhole
    ) {
        RXSpaceShipStatistics statistics = statisticsConfig.getRxSpaceShipStatistics();
        List<SpaceShip> batch = spaseShipsConfig.getSmallBatch();

        OverallStatistics result = statistics.calculate(batch);

        blackhole.consume(result);
    }

    @Benchmark
    public void mediumBatchRun(
            StatisticsConfig statisticsConfig,
            SpaseShipsConfig spaseShipsConfig,
            Blackhole blackhole
    ) {
        RXSpaceShipStatistics statistics = statisticsConfig.getRxSpaceShipStatistics();
        List<SpaceShip> batch = spaseShipsConfig.getMediumBatch();

        OverallStatistics result = statistics.calculate(batch);

        blackhole.consume(result);
    }

    @Benchmark
    public void largeBatchRun(
            StatisticsConfig statisticsConfig,
            SpaseShipsConfig spaseShipsConfig,
            Blackhole blackhole
    ) {
        RXSpaceShipStatistics statistics = statisticsConfig.getRxSpaceShipStatistics();
        List<SpaceShip> batch = spaseShipsConfig.getLargeBatch();

        OverallStatistics result = statistics.calculate(batch);

        blackhole.consume(result);
    }

    @Override
    protected String getReportPath() {
        return "reports/benchmarks/Overall/rx/result.txt";
    }
}
