package ru.itmo.spaceships.generator;

import ru.itmo.spaceships.model.RepairmanEntity;

import java.util.Random;

/**
 * Генератор для RepairmanEntity.
 */
public class RepairmanGenerator implements Generator<RepairmanEntity> {

    /**
     * Все возможные должности ремонтников.
     */
    private static final String[] POSITIONS = {
            "Старший механик", "Механик", "Электронщик",
            "Сварщик", "Слесарь", "Инженер-ремонтник",
            "Мастер по ремонту", "Техник", "Специалист по диагностике"
    };

    private final Random random;
    private final NameGenerator nameGenerator;

    /**
     * Создаёт RepairmanGenerator с новым экземпляром Random.
     */
    public RepairmanGenerator() {
        this(new Random());
    }

    /**
     * Создаёт RepairmanGenerator с указанным экземпляром Random.
     *
     * @param random генератор случайных чисел
     */
    public RepairmanGenerator(Random random) {
        this.random = random;
        this.nameGenerator = new NameGenerator(random);
    }

    /**
     * Создаёт RepairmanGenerator с указанными экземплярами Random и NameGenerator.
     *
     * @param random генератор случайных чисел
     * @param nameGenerator генератор имён
     */
    public RepairmanGenerator(Random random, NameGenerator nameGenerator) {
        this.random = random;
        this.nameGenerator = nameGenerator;
    }

    @Override
    public RepairmanEntity generateOne() {
        String name = nameGenerator.generateFullName();
        String position = POSITIONS[random.nextInt(POSITIONS.length)];

        // ID будет автоматически сгенерирован базой данных
        return new RepairmanEntity(0L, name, position);
    }
}

