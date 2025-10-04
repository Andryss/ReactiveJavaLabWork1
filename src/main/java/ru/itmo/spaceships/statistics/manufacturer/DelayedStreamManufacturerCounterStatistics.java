package ru.itmo.spaceships.statistics.manufacturer;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import ru.itmo.spaceships.model.SpaceShip;
import ru.itmo.spaceships.statistics.StatisticsCalculator;

/**
 * Класс для сбора статистики о количестве произведенных кораблей различными производителями.
 * При помощи Stream API (с задержкой)
 */
public class DelayedStreamManufacturerCounterStatistics implements StatisticsCalculator<SpaceShip, Map<String, Long>> {

    public static final long GET_MANUFACTURER_DELAY_MS = 3;

    @Override
    public Map<String, Long> calculate(List<SpaceShip> objects) {
        return objects.stream()
                .map(ship -> ship.getManufacturer(GET_MANUFACTURER_DELAY_MS))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }
}
