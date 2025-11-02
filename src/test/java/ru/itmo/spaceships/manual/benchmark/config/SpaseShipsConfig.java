package ru.itmo.spaceships.manual.benchmark.config;

import java.util.List;

import lombok.Getter;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import ru.itmo.spaceships.generator.SpaseShipGenerator;
import ru.itmo.spaceships.model.SpaceShip;

/**
 * Состояние со всеми группами объектов.
 * Вынесено отдельно, чтобы не тратить время на генерацию во время измерений
 */
@Getter
@State(Scope.Benchmark)
public class SpaseShipsConfig {

    private List<SpaceShip> milliBatch;
    private List<SpaceShip> smallBatch;
    private List<SpaceShip> mediumBatch;
    private List<SpaceShip> largeBatch;

    // Размер пачки, при котором сбор статистики БЕЗ ЗАДЕРЖКИ последовательным и параллельным способами занимают одинаковое время
    private List<SpaceShip> withoutDelayBatch;
    // Размер пачки, при котором сбор статистики С ЗАДЕРЖКОЙ последовательным и параллельным способами занимают одинаковое время
    private List<SpaceShip> withDelayBatch;

    // Размеры пачки для сравнения реактивных и параллельных потоков
    private List<SpaceShip> rxCompareSmall;
    private List<SpaceShip> rxCompareMedium;

    @Setup(Level.Trial)
    public void setUp(RandomConfig randomConfig) {
        SpaseShipGenerator generator = new SpaseShipGenerator(randomConfig.getSeededRandom());
        milliBatch = generator.generateMany(300);
        smallBatch = generator.generateMany(5_000);
        mediumBatch = generator.generateMany(50_000);
        largeBatch = generator.generateMany(250_000);

        withoutDelayBatch = generator.generateMany(3000);
        withDelayBatch = generator.generateMany(1); // NOTE: при размере больше 1 parallel всегда выигрывает

        rxCompareSmall = generator.generateMany(500);
        rxCompareMedium = generator.generateMany(2_000);
    }
}
