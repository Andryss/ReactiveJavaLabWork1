package ru.itmo.spaceships.generator;

import java.util.Random;

/**
 * Утилитный класс для генерации случайных имён.
 * Извлечён из CrewMemberGenerator для повторного использования.
 */
public class NameGenerator {

    /**
     * Все возможные имена.
     */
    private static final String[] FIRST_NAMES = {
            "Иван", "Алексей", "Дмитрий", "Сергей", "Павел",
            "Фёдор", "Александр", "Евгений", "Олег", "Артём"
    };

    /**
     * Все возможные фамилии.
     */
    private static final String[] LAST_NAMES = {
            "Иванов", "Петров", "Сидоров", "Кузнецов", "Смирнов",
            "Соколов", "Попов", "Морозов", "Волков", "Фёдоров"
    };

    private final Random random;

    /**
     * Создаёт NameGenerator с новым экземпляром Random.
     */
    public NameGenerator() {
        this(new Random());
    }

    /**
     * Создаёт NameGenerator с указанным экземпляром Random.
     *
     * @param random генератор случайных чисел
     */
    public NameGenerator(Random random) {
        this.random = random;
    }

    /**
     * Генерирует случайное полное имя (имя + фамилия).
     *
     * @return сгенерированное полное имя
     */
    public String generateFullName() {
        String firstName = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
        String lastName = LAST_NAMES[random.nextInt(LAST_NAMES.length)];
        return String.format("%s %s", firstName, lastName);
    }
}

