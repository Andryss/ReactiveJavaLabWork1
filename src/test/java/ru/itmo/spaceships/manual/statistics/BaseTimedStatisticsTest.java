package ru.itmo.spaceships.manual.statistics;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import lombok.extern.slf4j.Slf4j;
import ru.itmo.spaceships.generator.Generator;
import ru.itmo.spaceships.statistics.StatisticsCalculator;
import ru.itmo.spaceships.time.Timer;

@Slf4j
public abstract class BaseTimedStatisticsTest {

    public <T, R> void calculate(
            Generator<T> generator,
            int count,
            StatisticsCalculator<T, R> statisticsCalculator
    ) {
        List<T> generated = generator.generateMany(count);
        AtomicReference<R> result = new AtomicReference<>();

        Timer timer = new Timer();
        Runnable statisticsRunnable = () -> result.set(statisticsCalculator.calculate(generated));

        log.info("Start statistics calculating");
        long time = timer.time(statisticsRunnable);
        log.info("Calculated time: {} ns", time);

        log.info("Calculated result: {}", result.get());
    }
}
