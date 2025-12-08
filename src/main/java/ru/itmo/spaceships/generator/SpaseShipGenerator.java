package ru.itmo.spaceships.generator;

import java.time.Instant;
import java.util.List;
import java.util.Random;

import ru.itmo.spaceships.model.CrewMember;
import ru.itmo.spaceships.model.Dimensions;
import ru.itmo.spaceships.model.Engine;
import ru.itmo.spaceships.model.SpaceShipEntity;
import ru.itmo.spaceships.model.SpaceShipType;

/**
 * Генератор космических кораблей ({@link SpaceShipEntity})
 */
public class SpaseShipGenerator implements Generator<SpaceShipEntity> {

    /**
     * Все возможные производители кораблей
     */
    private static final String[] MANUFACTURERS = {
            "AMD", "Intel", "NVidia", "Acer", "Apple Inc.", "Hitachi", "IBM", "Qualcomm", "Samsung", "SpaceX", "NASA"
    };
    /**
     * Максимальное количество в секундах от сегодняшнего дня для времени производства лодки
     */
    private static final int MANUFACTURE_SECONDS_MAX = 1_000_000;
    /**
     * Минимальный и минимальный номер из названия корабля
     */
    private static final int MIN_NAME_NUMBER = 1_000;
    private static final int MAX_NAME_NUMBER = 9_000;
    /**
     * Минимальное и максимальное количество человек состава экипажа
     */
    private static final int MIN_CREW_SIZE = 10;
    private static final int MAX_CREW_SIZE = 20;
    /**
     * Минимальная и максимальная предельная развиваемая скорость корабля
     */
    private static final int MIN_MAX_SPEED = 1_000;
    private static final int MAX_MAX_SPEED = 10_000;

    private final Random random;
    private final Generator<Dimensions> dimensionsGenerator;
    private final Generator<Engine> engineGenerator;
    private final Generator<CrewMember> crewMemberGenerator;

    public SpaseShipGenerator() {
        this(new Random(), new DimensionsGenerator(), new EngineGenerator(), new CrewMemberGenerator());
    }

    public SpaseShipGenerator(Random random) {
        this(random, new DimensionsGenerator(random), new EngineGenerator(random), new CrewMemberGenerator(random));
    }

    public SpaseShipGenerator(Random random, Generator<Dimensions> dimensionsGenerator,
                              Generator<Engine> engineGenerator, Generator<CrewMember> crewMemberGenerator) {
        this.random = random;
        this.dimensionsGenerator = dimensionsGenerator;
        this.engineGenerator = engineGenerator;
        this.crewMemberGenerator = crewMemberGenerator;
    }

    @Override
    public SpaceShipEntity generateOne() {
        long serial = random.nextLong();

        String manufacturer = MANUFACTURERS[random.nextInt(MANUFACTURERS.length)];

        Instant manufactureDate = Instant.now().minusSeconds(random.nextInt(MANUFACTURE_SECONDS_MAX));

        String name = String.format("SS-%s", random.nextInt(MIN_NAME_NUMBER, MAX_NAME_NUMBER));

        SpaceShipType[] spaceShipTypes = SpaceShipType.values();
        SpaceShipType spaceShipType = spaceShipTypes[random.nextInt(spaceShipTypes.length)];

        Dimensions dimensions = dimensionsGenerator.generateOne();

        Engine engine = engineGenerator.generateOne();

        List<CrewMember> crewMembers = crewMemberGenerator.generateMany(random.nextInt(MIN_CREW_SIZE, MAX_CREW_SIZE));

        int maxSpeed = random.nextInt(MIN_MAX_SPEED, MAX_MAX_SPEED);

        return new SpaceShipEntity(serial, manufacturer, manufactureDate, name, spaceShipType, dimensions, engine,
                crewMembers, maxSpeed);
    }
}
