package ru.itmo.spaceships.manual.benchmark.config;

import java.util.Random;

import lombok.Getter;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@Getter
@State(Scope.Benchmark)
public class RandomConfig {
    private final Random seededRandom = new Random(1758979187L);
}
