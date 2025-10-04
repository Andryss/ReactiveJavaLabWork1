package ru.itmo.spaceships.manual.benchmark.manufacturer;

import java.util.List;
import java.util.Map;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;
import ru.itmo.spaceships.manual.benchmark.BaseBenchmarkTest;
import ru.itmo.spaceships.manual.benchmark.config.SpaseShipsConfig;
import ru.itmo.spaceships.manual.benchmark.config.StatisticsConfig;
import ru.itmo.spaceships.model.SpaceShip;
import ru.itmo.spaceships.statistics.manufacturer.DelayedStreamManufacturerCounterStatistics;

public class ManualDelayedStreamManufacturerCounterStatisticsTest extends BaseBenchmarkTest {

    @Benchmark
    public void milliBatchRun(
            StatisticsConfig statisticsConfig,
            SpaseShipsConfig spaseShipsConfig,
            Blackhole blackhole
    ) {
        DelayedStreamManufacturerCounterStatistics statistics =
                statisticsConfig.getDelayedStreamManufacturerCounterStatistics();
        List<SpaceShip> batch = spaseShipsConfig.getMilliBatch();

        Map<String, Long> result = statistics.calculate(batch);

        blackhole.consume(result);
    }

    @Override
    protected String getReportPath() {
        return "reports/benchmarks/ManufacturerCounterStatistics/stream/result-delay.txt";
    }
}
