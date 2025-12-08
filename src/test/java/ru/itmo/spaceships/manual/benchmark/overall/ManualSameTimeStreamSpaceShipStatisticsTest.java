package ru.itmo.spaceships.manual.benchmark.overall;

import org.junit.jupiter.api.Disabled;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;
import ru.itmo.spaceships.manual.benchmark.BaseBenchmarkTest;
import ru.itmo.spaceships.manual.benchmark.config.SpaseShipsConfig;
import ru.itmo.spaceships.manual.benchmark.config.StatisticsConfig;
import ru.itmo.spaceships.model.SpaceShipEntity;
import ru.itmo.spaceships.statistics.overall.ConcurrentStreamSpaceShipStatistics;
import ru.itmo.spaceships.statistics.overall.OverallStatistics;
import ru.itmo.spaceships.statistics.overall.SequenceStreamSpaceShipStatistics;

import java.util.List;

@Disabled("Только для ручного запуска")
public class ManualSameTimeStreamSpaceShipStatisticsTest extends BaseBenchmarkTest {

    @Benchmark
    public void sequenceWithoutDelayBatchRun(
            StatisticsConfig statisticsConfig,
            SpaseShipsConfig spaseShipsConfig,
            Blackhole blackhole
    ) {
        SequenceStreamSpaceShipStatistics statistics = statisticsConfig.getSequenceStreamSpaceShipStatistics();
        List<SpaceShipEntity> batch = spaseShipsConfig.getWithoutDelayBatch();

        OverallStatistics result = statistics.calculate(batch);

        blackhole.consume(result);
    }

    @Benchmark
    public void parallelWithoutDelayBatchRun(
            StatisticsConfig statisticsConfig,
            SpaseShipsConfig spaseShipsConfig,
            Blackhole blackhole
    ) {
        ConcurrentStreamSpaceShipStatistics statistics =
                statisticsConfig.getConcurrentStreamSpaceShipStatistics();
        List<SpaceShipEntity> batch = spaseShipsConfig.getWithoutDelayBatch();

        OverallStatistics result = statistics.calculate(batch);

        blackhole.consume(result);
    }

    @Benchmark
    public void sequenceWithDelayBatchRun(
            StatisticsConfig statisticsConfig,
            SpaseShipsConfig spaseShipsConfig,
            Blackhole blackhole
    ) {
        SequenceStreamSpaceShipStatistics statistics =
                statisticsConfig.getSequenceDelayedStreamSpaceShipStatistics();
        List<SpaceShipEntity> batch = spaseShipsConfig.getWithDelayBatch();

        OverallStatistics result = statistics.calculate(batch);

        blackhole.consume(result);
    }

    @Benchmark
    public void parallelWithDelayBatchRun(
            StatisticsConfig statisticsConfig,
            SpaseShipsConfig spaseShipsConfig,
            Blackhole blackhole
    ) {
        ConcurrentStreamSpaceShipStatistics statistics =
                statisticsConfig.getConcurrentDelayedStreamSpaceShipStatistics();
        List<SpaceShipEntity> batch = spaseShipsConfig.getWithDelayBatch();

        OverallStatistics result = statistics.calculate(batch);

        blackhole.consume(result);
    }

    @Override
    protected String getReportPath() {
        return "reports/benchmarks/Overall/stream/result-same-time.txt";
    }
}
