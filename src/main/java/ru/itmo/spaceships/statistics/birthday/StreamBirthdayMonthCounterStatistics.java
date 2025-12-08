package ru.itmo.spaceships.statistics.birthday;

import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import ru.itmo.spaceships.model.SpaceShipEntity;
import ru.itmo.spaceships.statistics.StatisticsCalculator;

/**
 * Класс для сбора статистики о количестве членов экипажа, рожденных в разные месяцы.
 * При помощи Stream API
 */
public class StreamBirthdayMonthCounterStatistics implements StatisticsCalculator<SpaceShipEntity, Map<Month, Long>> {

    @Override
    public Map<Month, Long> calculate(List<SpaceShipEntity> objects) {
        return objects.stream()
                .parallel()
                .flatMap(ship -> ship.getCrew().stream())
                .map(crewMember -> crewMember.getBirthDate().getMonth())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }
}
