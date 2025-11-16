package ru.itmo.spaceships.manual.benchmark.overall;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;
import ru.itmo.spaceships.manual.benchmark.BaseBenchmarkTest;
import ru.itmo.spaceships.manual.benchmark.config.SpaseShipsConfig;
import ru.itmo.spaceships.manual.benchmark.config.StatisticsConfig;
import ru.itmo.spaceships.model.SpaceShip;
import ru.itmo.spaceships.statistics.overall.OverallStatistics;
import ru.itmo.spaceships.statistics.overall.RXBackpressureShipStatistics;


import java.util.List;

public class ManualRxBackpessureSpaceShipStatisticsTest extends BaseBenchmarkTest {

    @Benchmark
    public void largeBatchRun(
            StatisticsConfig statisticsConfig,
            SpaseShipsConfig spaseShipsConfig,
            Blackhole blackhole
    ) {
        RXBackpressureShipStatistics statistics = statisticsConfig.getRxBackpressureShipStatistics();
        List<SpaceShip> batch = spaseShipsConfig.getLargeBatch();

        OverallStatistics result = statistics.calculate(batch);

        blackhole.consume(result);
    }

    @Override
    protected String getReportPath() {
        return "reports/benchmarks/Overall/rx/backpressure.txt";
    }
}
