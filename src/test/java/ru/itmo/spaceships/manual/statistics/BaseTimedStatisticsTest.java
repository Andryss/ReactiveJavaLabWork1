package ru.itmo.spaceships.manual.statistics;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ru.itmo.spaceships.generator.Generator;
import ru.itmo.spaceships.statistics.StatisticsCalculator;
import ru.itmo.spaceships.time.Timer;

@Slf4j
public abstract class BaseTimedStatisticsTest {

    @SneakyThrows
    public <T, R> void calculate(
            Generator<T> generator,
            int count,
            StatisticsCalculator<T, R> statisticsCalculator,
            String reportPath
    ) {
        Files.createDirectories(Paths.get(reportPath).getParent());

        try (FileWriter writer = new FileWriter(reportPath, false)) {
            writer.write(String.format("Генератор:     %s\n", generator.getClass().getSimpleName()));
            writer.write(String.format("Количество:    %s\n", count));
            writer.write(String.format("Статистика:    %s\n", statisticsCalculator.getClass().getSimpleName()));

            List<T> generated = generator.generateMany(count);
            AtomicReference<R> result = new AtomicReference<>();

            Timer timer = new Timer();
            Runnable statisticsRunnable = () -> result.set(statisticsCalculator.calculate(generated));

            log.info("Start statistics calculating");
            long time = timer.time(statisticsRunnable);
            log.info("Calculated time: {} ns", time);

            log.info("Calculated result: {}", result.get());

            writer.write(String.format("Время:         %s\n", formatHumanReadable(time)));
            writer.write(String.format("Результат:     %s\n", result.get()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String formatHumanReadable(long nanos) {
        if (nanos < 1000) {
            return nanos + " ns";
        }

        long micros = nanos / 1000;
        if (micros < 1000) {
            return micros + " µs";
        }

        long millis = micros / 1000;
        if (millis < 1000) {
            return millis + " ms";
        }

        return millis / 1000 + " s";
    }
}
