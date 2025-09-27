package ru.itmo.spaceships.manual.benchmark.manufacturer;

import java.util.List;
import java.util.Map;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;
import ru.itmo.spaceships.manual.benchmark.BaseBenchmark;
import ru.itmo.spaceships.manual.benchmark.BenchmarkRunner;
import ru.itmo.spaceships.manual.benchmark.config.SpaseShipsConfig;
import ru.itmo.spaceships.manual.benchmark.config.StatisticsConfig;
import ru.itmo.spaceships.model.SpaceShip;
import ru.itmo.spaceships.statistics.manufacturer.CycleManufacturerCounterStatistics;

public class ManualCycleManufacturerCounterStatisticsTest extends BaseBenchmark {

    @Benchmark
    public void smallBatchRun(
            StatisticsConfig statisticsConfig,
            SpaseShipsConfig spaseShipsConfig,
            Blackhole blackhole
    ) {
        CycleManufacturerCounterStatistics statistics = statisticsConfig.getCycleManufacturerCounterStatistics();
        List<SpaceShip> batch = spaseShipsConfig.getSmallBatch();

        Map<String, Long> result = statistics.calculate(batch);

        blackhole.consume(result);
    }

    public static void main(String[] args) {
        BenchmarkRunner.run(
                ManualCycleManufacturerCounterStatisticsTest.class,
                "reports/benchmarks/ManufacturerCounterStatistics/cycle/result.txt"
        );
    }
}
