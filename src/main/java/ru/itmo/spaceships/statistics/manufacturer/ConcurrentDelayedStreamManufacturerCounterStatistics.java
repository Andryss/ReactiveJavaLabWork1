package ru.itmo.spaceships.statistics.manufacturer;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import ru.itmo.spaceships.model.SpaceShip;
import ru.itmo.spaceships.statistics.StatisticsCalculator;

import static ru.itmo.spaceships.statistics.manufacturer.DelayedStreamManufacturerCounterStatistics.GET_MANUFACTURER_DELAY_MS;

/**
 * Класс для сбора статистики о количестве произведенных кораблей различными производителями.
 * При помощи Stream API (параллельно + с задержкой)
 */
public class ConcurrentDelayedStreamManufacturerCounterStatistics
        implements StatisticsCalculator<SpaceShip, Map<String, Long>> {

    @Override
    public Map<String, Long> calculate(List<SpaceShip> objects) {
        return objects.stream()
                .parallel()
                .map(ship -> ship.getManufacturer(GET_MANUFACTURER_DELAY_MS))
                .collect(Collectors.groupingByConcurrent(Function.identity(), Collectors.counting()));
    }
}
