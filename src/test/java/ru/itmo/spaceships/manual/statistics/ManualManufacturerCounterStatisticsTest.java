package ru.itmo.spaceships.manual.statistics;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import ru.itmo.spaceships.generator.SpaseShipGenerator;
import ru.itmo.spaceships.statistics.SimpleManufacturerCounterStatistics;

@Slf4j
@Disabled("Только для ручного тестирования")
public class ManualManufacturerCounterStatisticsTest extends BaseTimedStatisticsTest {

    @Test
    void simpleSmallTest() {
        calculate(new SpaseShipGenerator(), 5_000, new SimpleManufacturerCounterStatistics());
    }

    @Test
    void simpleMediumTest() {
        calculate(new SpaseShipGenerator(), 50_000, new SimpleManufacturerCounterStatistics());
    }

    @Test
    void simpleLargeTest() {
        calculate(new SpaseShipGenerator(), 250_000, new SimpleManufacturerCounterStatistics());
    }
}
