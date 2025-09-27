package ru.itmo.spaceships.manual.benchmark.config;

import lombok.Getter;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import ru.itmo.spaceships.statistics.manufacturer.CycleManufacturerCounterStatistics;

@Getter
@State(Scope.Benchmark)
public class StatisticsConfig {
    private final CycleManufacturerCounterStatistics cycleManufacturerCounterStatistics = new CycleManufacturerCounterStatistics();
}
