package ru.itmo.spaceships.statistics.birthday;

import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.itmo.spaceships.model.CrewMember;
import ru.itmo.spaceships.model.SpaceShip;
import ru.itmo.spaceships.statistics.StatisticsCalculator;

/**
 * Класс для сбора статистики о количестве членов экипажа, рожденных в разные месяцы.
 * При помощи стандартного цикла
 */
public class CycleBirthdayMonthCounterStatistics implements StatisticsCalculator<SpaceShip, Map<Month, Long>> {

    @Override
    public Map<Month, Long> calculate(List<SpaceShip> objects) {
        Map<Month, Long> result = new HashMap<>();
        for (SpaceShip ship : objects) {
            List<CrewMember> crew = ship.getCrew();
            for (CrewMember crewMember : crew) {
                Month month = crewMember.getBirthDate().getMonth();
                result.merge(month, 1L, Long::sum);
            }
        }
        return result;
    }
}
