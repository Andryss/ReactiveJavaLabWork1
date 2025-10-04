package ru.itmo.spaceships.manual.benchmark.config;

import java.util.Random;

import lombok.Getter;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

/**
 * Состояние, определяющее случайную генерацию.
 * Вынесено отдельно, чтобы "контролировать" случайность
 * и подавать всем алгоритмам на вход одинаковые сгенерированные данные
 */
@Getter
@State(Scope.Benchmark)
public class RandomConfig {
    private final Random seededRandom = new Random(1758979187L);
}
