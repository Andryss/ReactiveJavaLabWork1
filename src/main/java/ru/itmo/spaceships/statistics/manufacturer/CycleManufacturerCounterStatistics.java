package ru.itmo.spaceships.statistics.manufacturer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.itmo.spaceships.model.SpaceShipEntity;
import ru.itmo.spaceships.statistics.StatisticsCalculator;

/**
 * Класс для сбора статистики о количестве произведенных кораблей различными производителями.
 * При помощи стандартного цикла
 */
public class CycleManufacturerCounterStatistics implements StatisticsCalculator<SpaceShipEntity, Map<String, Long>> {

    @Override
    public Map<String, Long> calculate(List<SpaceShipEntity> objects) {
        Map<String, Long> result = new HashMap<>();
        for (SpaceShipEntity ship : objects) {
            String manufacturer = ship.getManufacturer();
            Long count = result.get(manufacturer);
            result.put(manufacturer, count == null ? 1 : count + 1);
        }
        return result;
    }
}
