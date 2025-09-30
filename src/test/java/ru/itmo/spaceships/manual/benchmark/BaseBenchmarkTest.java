package ru.itmo.spaceships.manual.benchmark;


import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Warmup;

/**
 * Базовые настройки для запуска бенчмарков. Менять на свой вкус и цвет
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(value = 2, warmups = 1)
@Warmup(iterations = 3, time = 4)
@Measurement(iterations = 3, time = 8)
public abstract class BaseBenchmarkTest {

    @Test
    public void run() {
        BenchmarkRunner.run(getClass(), getReportPath());
    }

    protected abstract String getReportPath();
}
