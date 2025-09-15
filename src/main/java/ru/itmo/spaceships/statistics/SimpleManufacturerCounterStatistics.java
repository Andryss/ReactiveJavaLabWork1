package ru.itmo.spaceships.statistics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.itmo.spaceships.model.SpaceShip;

/**
 * Класс для сбора статистики о количестве произведенных кораблей различными производителями.
 * При помощи стандартного цикла
 */
public class SimpleManufacturerCounterStatistics implements StatisticsCalculator<SpaceShip, Map<String, Long>> {

    @Override
    public Map<String, Long> calculate(List<SpaceShip> objects) {
        Map<String, Long> result = new HashMap<>();
        for (SpaceShip ship : objects) {
            String manufacturer = ship.getManufacturer();
            Long count = result.get(manufacturer);
            result.put(manufacturer, count == null ? 1 : count + 1);
        }
        return result;
    }
}
