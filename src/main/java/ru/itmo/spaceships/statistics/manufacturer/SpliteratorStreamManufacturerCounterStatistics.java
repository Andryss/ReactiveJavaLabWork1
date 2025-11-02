package ru.itmo.spaceships.statistics.manufacturer;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import ru.itmo.spaceships.model.SpaceShip;
import ru.itmo.spaceships.statistics.DelayedStatistics;
import ru.itmo.spaceships.statistics.SpaceShipSpliterator;
import ru.itmo.spaceships.statistics.StatisticsCalculator;

/**
 * Класс для сбора статистики о количестве произведенных кораблей различными производителями.
 * При помощи Stream API (параллельно + без задержки)
 */
public class SpliteratorStreamManufacturerCounterStatistics extends DelayedStatistics
        implements StatisticsCalculator<SpaceShip, Map<String, Long>> {

    public SpliteratorStreamManufacturerCounterStatistics(long delay) {
        super(delay);
    }

    public SpliteratorStreamManufacturerCounterStatistics() {
    }

    @Override
    public Map<String, Long> calculate(List<SpaceShip> objects) {
        return StreamSupport.stream(new SpaceShipSpliterator(objects), true)
                .map(ship -> ship.getManufacturer(getDelay()))
                .collect(Collectors.groupingByConcurrent(Function.identity(), Collectors.counting()));
    }
}
