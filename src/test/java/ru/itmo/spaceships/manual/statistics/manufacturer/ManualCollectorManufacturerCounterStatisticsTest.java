package ru.itmo.spaceships.manual.statistics.manufacturer;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import ru.itmo.spaceships.generator.SpaseShipGenerator;
import ru.itmo.spaceships.manual.statistics.BaseTimedStatisticsTest;
import ru.itmo.spaceships.statistics.manufacturer.CollectorManufacturerCounterStatistics;

@Slf4j
@Disabled("Только для ручного тестирования")
public class ManualCollectorManufacturerCounterStatisticsTest extends BaseTimedStatisticsTest {

    public static final String REPORTS_BASE_PATH = "reports/ManufacturerCounterStatistics/collector/result";

    @Test
    void streamSmallTest() {
        calculate(
                new SpaseShipGenerator(),
                5_000,
                new CollectorManufacturerCounterStatistics(),
                REPORTS_BASE_PATH + ".small"
        );
    }

    @Test
    void streamMediumTest() {
        calculate(
                new SpaseShipGenerator(),
                50_000,
                new CollectorManufacturerCounterStatistics(),
                REPORTS_BASE_PATH + ".medium"
        );
    }

    @Test
    void streamLargeTest() {
        calculate(
                new SpaseShipGenerator(),
                250_000,
                new CollectorManufacturerCounterStatistics(),
                REPORTS_BASE_PATH + ".large"
        );
    }
}
