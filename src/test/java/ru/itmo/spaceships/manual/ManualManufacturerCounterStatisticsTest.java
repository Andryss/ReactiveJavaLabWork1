package ru.itmo.spaceships.manual;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import ru.itmo.spaceships.generator.SpaseShipGenerator;
import ru.itmo.spaceships.model.SpaceShip;
import ru.itmo.spaceships.statistics.SimpleManufacturerCounterStatistics;
import ru.itmo.spaceships.time.Timer;

@Slf4j
@Disabled("Только для ручного тестирования")
public class ManualManufacturerCounterStatisticsTest {

    @Test
    void simpleSmallTest() {
        int size = 50_000;
        SpaseShipGenerator generator = new SpaseShipGenerator();
        SimpleManufacturerCounterStatistics statistics = new SimpleManufacturerCounterStatistics();
        Timer timer = new Timer();
        AtomicReference<Map<String, Long>> result = new AtomicReference<>();

        List<SpaceShip> ships = generator.generateMany(size);

        Runnable statisticsRunnable = () -> result.set(statistics.calculate(ships));

        log.info("Timing started");
        long time = timer.time(statisticsRunnable);
        log.info("Time {} ns", time);

        log.info("Result: {}", result.get());
    }

}
