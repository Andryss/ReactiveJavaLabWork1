package ru.itmo.spaceships.statistics.manufacturer;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import ru.itmo.spaceships.model.SpaceShipEntity;
import ru.itmo.spaceships.statistics.DelayedStatistics;
import ru.itmo.spaceships.statistics.StatisticsCalculator;

/**
 * Класс для сбора статистики о количестве произведенных кораблей различными производителями.
 * При помощи Stream API
 */
public class StreamManufacturerCounterStatistics extends DelayedStatistics
        implements StatisticsCalculator<SpaceShipEntity, Map<String, Long>> {

    public StreamManufacturerCounterStatistics(long delay) {
        super(delay);
    }

    public StreamManufacturerCounterStatistics() {
    }

    @Override
    public Map<String, Long> calculate(List<SpaceShipEntity> objects) {
        return objects.stream()
                .map(ship -> ship.getManufacturerDelayed(getDelay()))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }
}
