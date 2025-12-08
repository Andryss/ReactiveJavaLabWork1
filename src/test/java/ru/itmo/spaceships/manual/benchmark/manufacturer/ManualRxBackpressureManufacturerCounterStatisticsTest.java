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
import ru.itmo.spaceships.statistics.manufacturer.RxBackpressureManufacturerCounterStatistics;

@Disabled("Только для ручного запуска")
public class ManualRxBackpressureManufacturerCounterStatisticsTest extends BaseBenchmarkTest {

    @Benchmark
    public void smallBatchRun(
            StatisticsConfig statisticsConfig,
            SpaseShipsConfig spaseShipsConfig,
            Blackhole blackhole
    ) {
        RxBackpressureManufacturerCounterStatistics statistics =
                statisticsConfig.getRxBackpressureManufacturerCounterStatistics();
        List<SpaceShipEntity> batch = spaseShipsConfig.getSmallBatch();

        Map<String, Long> result = statistics.calculate(batch);

        blackhole.consume(result);
    }

    @Benchmark
    public void largeBatchRun(
            StatisticsConfig statisticsConfig,
            SpaseShipsConfig spaseShipsConfig,
            Blackhole blackhole
    ) {
        RxBackpressureManufacturerCounterStatistics statistics =
                statisticsConfig.getRxBackpressureManufacturerCounterStatistics();
        List<SpaceShipEntity> batch = spaseShipsConfig.getLargeBatch();

        Map<String, Long> result = statistics.calculate(batch);

        blackhole.consume(result);
    }

    @Override
    protected String getReportPath() {
        return "reports/benchmarks/ManufacturerCounterStatistics/rx/result-backpressure-2.txt";
    }
}
