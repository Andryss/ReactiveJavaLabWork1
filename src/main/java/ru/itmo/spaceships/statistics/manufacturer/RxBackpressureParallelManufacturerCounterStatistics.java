package ru.itmo.spaceships.statistics.manufacturer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.Pair;
import ru.itmo.spaceships.model.SpaceShip;
import ru.itmo.spaceships.statistics.StatisticsCalculator;

/**
 * Класс для сбора статистики о количестве произведенных кораблей различными производителями.
 * При помощи RxJava
 */
@Slf4j
@RequiredArgsConstructor
public class RxBackpressureParallelManufacturerCounterStatistics implements StatisticsCalculator<SpaceShip, Map<String, Long>> {

    private final int bufferSize;

    @Override
    public Map<String, Long> calculate(List<SpaceShip> objects) {
        return Flowable.<SpaceShip>create(emitter -> {
                    for (SpaceShip object : objects) {
                        emitter.onNext(object);
                    }
                    emitter.onComplete();
                }, BackpressureStrategy.BUFFER)
                .window(bufferSize)
                .flatMapSingle(flow -> flow
                        .subscribeOn(Schedulers.computation())
                        .groupBy(SpaceShip::getManufacturer)
                        .flatMapSingle(grouped -> grouped
                                .count()
                                .map(count -> Pair.create(grouped.getKey(), count))
                        )
                        .toMap(Pair::getKey, Pair::getValue)
                )
                .reduce(
                        new HashMap<String, Long>(),
                        (map, windowResult) -> {
                            windowResult.forEach((key, value) -> map.merge(key, value, Long::sum));
                            return map;
                        }
                )
                .blockingGet();
    }
}
