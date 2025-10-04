package ru.itmo.spaceships.statistics.manufacturer;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import ru.itmo.spaceships.model.SpaceShip;
import ru.itmo.spaceships.statistics.SpaceShipSpliterator;
import ru.itmo.spaceships.statistics.StatisticsCalculator;

/**
 * Класс для сбора статистики о количестве произведенных кораблей различными производителями.
 * При помощи Stream API (параллельно + без задержки)
 */
public class SpliteratorStreamManufacturerCounterStatistics
        implements StatisticsCalculator<SpaceShip, Map<String, Long>> {

    @Override
    public Map<String, Long> calculate(List<SpaceShip> objects) {
        return StreamSupport.stream(new SpaceShipSpliterator(objects), true)
                .map(SpaceShip::getManufacturer)
                .collect(Collectors.groupingByConcurrent(Function.identity(), Collectors.counting()));
    }
}
