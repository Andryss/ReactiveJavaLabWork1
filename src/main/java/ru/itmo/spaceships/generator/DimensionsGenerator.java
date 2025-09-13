package ru.itmo.spaceships.generator;

import java.util.Random;

import ru.itmo.spaceships.model.Dimensions;

/**
 * Генератор ВГХ ({@link Dimensions})
 */
public class DimensionsGenerator implements Generator<Dimensions> {

    /**
     * Минимальная и максимальная длина корабля в метрах
     */
    private static final long LENGTH_MIN = 15;
    private static final long LENGTH_MAX = 300;
    /**
     * Минимальное и максимальное соотношение ширины к длине
     */
    private static final double WIDTH_COEFFICIENT_MIN = 0.3;
    private static final double WIDTH_COEFFICIENT_MAX = 0.9;
    /**
     * Минимальное и максимальное соотношение высоты к длине
     */
    private static final double HEIGHT_COEFFICIENT_MIN = 0.2;
    private static final double HEIGHT_COEFFICIENT_MAX = 0.6;
    /**
     * Минимальная и максимальная заполненность объема корабля от сырого объема.
     * Пример: длина 10 метров, ширина 5 метров, высота 2 метра. Сырой объем 10 * 5 * 2 = 100 кубических метров.
     * Заполненность 0.5, значит фактически корабль занимает только половину сырого объема, то есть 50 кубометров.
     */
    private static final double VOLUME_FULLNESS_MIN = 0.3;
    private static final double VOLUME_FULLNESS_MAX = 0.6;
    /**
     * Минимальная и максимальная плотность фактического объема корабля в тоннах на кубометр
     */
    private static final double DENSITY_MIN = 0.003;
    private static final double DENSITY_MAX = 0.010;

    private final Random random;

    public DimensionsGenerator() {
        this(new Random());
    }

    public DimensionsGenerator(Random random) {
        this.random = random;
    }

    @Override
    public Dimensions generateOne() {
        long length = random.nextLong(LENGTH_MIN, LENGTH_MAX);
        long width = (long) (length * random.nextDouble(WIDTH_COEFFICIENT_MIN, WIDTH_COEFFICIENT_MAX));
        long height = (long) (length * random.nextDouble(HEIGHT_COEFFICIENT_MIN, HEIGHT_COEFFICIENT_MAX));

        long volumeRaw = length * width * height;
        double volume = volumeRaw * random.nextDouble(VOLUME_FULLNESS_MIN, VOLUME_FULLNESS_MAX);

        double weight = volume * random.nextDouble(DENSITY_MIN, DENSITY_MAX);

        return new Dimensions(length, width, height, weight, volume);
    }
}
