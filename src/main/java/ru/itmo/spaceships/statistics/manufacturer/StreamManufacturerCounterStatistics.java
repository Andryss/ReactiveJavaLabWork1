package ru.itmo.spaceships.statistics.manufacturer;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import ru.itmo.spaceships.model.SpaceShip;
import ru.itmo.spaceships.statistics.StatisticsCalculator;

/**
 * Класс для сбора статистики о количестве произведенных кораблей различными производителями.
 * При помощи Stream API
 */
public class StreamManufacturerCounterStatistics implements StatisticsCalculator<SpaceShip, Map<String, Long>> {

    @Override
    public Map<String, Long> calculate(List<SpaceShip> objects) {
        return objects.stream()
                .parallel()
                .map(SpaceShip::getManufacturer)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }
}
