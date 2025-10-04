package ru.itmo.spaceships.manual.statistics.overall;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import ru.itmo.spaceships.generator.SpaseShipGenerator;
import ru.itmo.spaceships.manual.statistics.BaseTimedStatisticsTest;
import ru.itmo.spaceships.statistics.overall.CycleSpaceShipStatistics;

@Slf4j
@Disabled("Только для ручного тестирования")
public class CycleSpaceShipStatisticsTest extends BaseTimedStatisticsTest {

    public static final String REPORTS_BASE_PATH = "reports/OverallStatistics/cycle/result";

    @Test
    void simpleSmallTest() {
        calculate(
                new SpaseShipGenerator(),
                5_000,
                new CycleSpaceShipStatistics(),
                REPORTS_BASE_PATH + ".small"
        );
    }

    @Test
    void simpleMediumTest() {
        calculate(
                new SpaseShipGenerator(),
                50_000,
                new CycleSpaceShipStatistics(),
                REPORTS_BASE_PATH + ".medium"
        );
    }

    @Test
    void simpleLargeTest() {
        calculate(
                new SpaseShipGenerator(),
                250_000,
                new CycleSpaceShipStatistics(),
                REPORTS_BASE_PATH + ".large"
        );
    }
}
