package ru.itmo.spaceships.generator;

import java.util.Random;

import ru.itmo.spaceships.model.Engine;
import ru.itmo.spaceships.model.FuelType;

/**
 * Генератор двигателей ({@link Engine})
 */
public class EngineGenerator implements Generator<Engine> {

    /**
     * Все возможные производители двигателей
     */
    private static final String[] MANUFACTURERS = {
            "GAZ", "UAZ", "Kuznetsov", "ELSIB", "Saturn", "Kamaz", "RUMO"
    };
    /**
     * Минимальный и максимальный номер модели двигателя
     */
    private static final int MIN_MODEL_NUM = 1;
    private static final int MAX_MODEL_NUM = 5;
    /**
     * Минимальная и максимальная тяга
     */
    private static final int MIN_THRUST = 50;
    private static final int MAX_THRUST = 500;
    /**
     * Минимальное и максимальное потребление топлива
     */
    private static final double MIN_FUEL_CONSUMPTION = 0.5;
    private static final double MAX_FUEL_CONSUMPTION = 10.0;

    private final Random random;

    public EngineGenerator() {
        this(new Random());
    }

    public EngineGenerator(Random random) {
        this.random = random;
    }

    @Override
    public Engine generateOne() {
        String manufacturer = MANUFACTURERS[random.nextInt(MANUFACTURERS.length)];
        String model = String.format("%s Model-%s", manufacturer, random.nextInt(MIN_MODEL_NUM, MAX_MODEL_NUM));

        int thrust = random.nextInt(MIN_THRUST, MAX_THRUST);

        FuelType[] fuelTypes = FuelType.values();
        FuelType fuelType = fuelTypes[random.nextInt(fuelTypes.length)];

        double fuelConsumption = random.nextDouble(MIN_FUEL_CONSUMPTION, MAX_FUEL_CONSUMPTION);

        return new Engine(model, thrust, fuelType, fuelConsumption);
    }
}
