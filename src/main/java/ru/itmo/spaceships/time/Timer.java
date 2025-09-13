package ru.itmo.spaceships.time;

import java.math.BigInteger;

import lombok.extern.slf4j.Slf4j;

/**
 * Класс для расчета времени выполнения какой-то задачи
 */
@Slf4j
public class Timer {

    private static final int HEATING_CYCLES = 1000;

    private static final int TIME_CYCLES = 1000;

    /**
     * Рассчитать время выполнения в наносекундах
     */
    public long time(Runnable task) {
        log.info("Heating start");
        for (int i = 0; i < HEATING_CYCLES; i++) {
            task.run();
        }
        log.info("{} heating cycles finished", HEATING_CYCLES);

        long[] results = new long[TIME_CYCLES];

        log.info("Time start");
        for (int i = 0; i < TIME_CYCLES; i++) {
            long startTime = System.nanoTime();
            task.run();
            long endTime = System.nanoTime();
            results[i] = endTime - startTime;
        }
        log.info("{} time cycles finished", TIME_CYCLES);

        log.info("Calculating exact average result");
        BigInteger sum = BigInteger.ZERO;
        for (long val : results) {
            sum = sum.add(BigInteger.valueOf(val));
        }
        long average = (long) sum.divide(BigInteger.valueOf(results.length)).doubleValue();
        log.info("Calculated average: {}", average);

        return average;
    }
}
