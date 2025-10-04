package ru.itmo.spaceships.manual.benchmark;

import java.io.File;
import java.io.IOException;

import lombok.SneakyThrows;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * Утилитарный класс для запуска бенчмарков и записи результата в файл
 */
public class BenchmarkRunner {

    @SneakyThrows
    public static void run(Class<?> cls, String reportPath) {
        createFileIfNotExist(reportPath);

        Options opt = new OptionsBuilder()
                .include(cls.getSimpleName())
                .result(reportPath)
                .resultFormat(ResultFormatType.TEXT)
                .build();

        new Runner(opt).run();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void createFileIfNotExist(String reportPath) throws IOException {
        File file = new File(reportPath);

        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        file.createNewFile();
    }
}
