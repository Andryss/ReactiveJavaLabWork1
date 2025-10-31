package ru.itmo.spaceships.statistics.manufacturer;

import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.apache.commons.math3.util.Pair;
import ru.itmo.spaceships.model.SpaceShip;
import ru.itmo.spaceships.statistics.StatisticsCalculator;

/**
 * Класс для сбора статистики о количестве произведенных кораблей различными производителями.
 * При помощи RxJava
 */
public class RxManufacturerCounterStatistics implements StatisticsCalculator<SpaceShip, Map<String, Long>> {

    @Override
    public Map<String, Long> calculate(List<SpaceShip> objects) {
        return Observable.fromIterable(objects)
                .subscribeOn(Schedulers.computation())
                .groupBy(SpaceShip::getManufacturer)
                .flatMapSingle(grouped -> grouped
                        .count()
                        .map(count -> Pair.create(grouped.getKey(), count))
                )
                .toMap(Pair::getKey, Pair::getValue)
                .blockingGet();
    }

}
