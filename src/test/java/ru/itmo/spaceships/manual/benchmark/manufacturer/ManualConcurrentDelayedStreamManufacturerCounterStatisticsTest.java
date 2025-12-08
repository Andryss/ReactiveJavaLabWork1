package ru.itmo.spaceships.manual.benchmark.manufacturer;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Disabled;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;
import ru.itmo.spaceships.manual.benchmark.BaseBenchmarkTest;
import ru.itmo.spaceships.manual.benchmark.config.SpaseShipsConfig;
import ru.itmo.spaceships.manual.benchmark.config.StatisticsConfig;
import ru.itmo.spaceships.model.SpaceShipEntity;
import ru.itmo.spaceships.statistics.manufacturer.ConcurrentStreamManufacturerCounterStatistics;

@Disabled("Только для ручного запуска")
public class ManualConcurrentDelayedStreamManufacturerCounterStatisticsTest extends BaseBenchmarkTest {

    @Benchmark
    public void milliBatchRun(
            StatisticsConfig statisticsConfig,
            SpaseShipsConfig spaseShipsConfig,
            Blackhole blackhole
    ) {
        ConcurrentStreamManufacturerCounterStatistics statistics =
                statisticsConfig.getConcurrentDelayedStreamManufacturerCounterStatistics();
        List<SpaceShipEntity> batch = spaseShipsConfig.getMilliBatch();

        Map<String, Long> result = statistics.calculate(batch);

        blackhole.consume(result);
    }

    @Override
    protected String getReportPath() {
        return "reports/benchmarks/ManufacturerCounterStatistics/stream/result-concurrent-delay.txt";
    }
}
