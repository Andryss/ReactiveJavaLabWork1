package ru.itmo.spaceships.manual.time;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import ru.itmo.spaceships.time.Timer;

@Slf4j
@Disabled("Только для ручного тестирования")
public class ManualTimerTest {

    @Test
    void timeTest() {
        int size = 8_000_000;
        Random random = new Random();

        long[] array = new long[size];
        for (int i = 0; i < array.length; i++) {
            array[i] = random.nextLong();
        }

        AtomicLong result = new AtomicLong();

        Runnable calcSumRunnable = () -> {
            long sum = 0;
            for (long l : array) {
                sum += l;
            }
            result.set(sum);
        };

        Timer timer = new Timer();

        int cycles = 10;
        for (int i = 0; i < cycles; i++) {
            long time = timer.time(calcSumRunnable);
            log.info("Cycle {}, result {}, time: {} ns", i, result.get(), time);
        }

        log.info("Final result {}", result.get());
    }
}
