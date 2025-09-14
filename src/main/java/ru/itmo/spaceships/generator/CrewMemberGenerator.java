package ru.itmo.spaceships.generator;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Random;

import ru.itmo.spaceships.model.CrewMember;

/**
 * Генератор членов экипажа ({@link CrewMember})
 */
public class CrewMemberGenerator implements Generator<CrewMember> {

    /**
     * Все возможные имена
     */
    private static final String[] FIRST_NAMES = {
            "Иван", "Алексей", "Дмитрий", "Сергей", "Павел",
            "Фёдор", "Александр", "Евгений", "Олег", "Артём"
    };
    /**
     * Все возможные фамилии
     */
    private static final String[] LAST_NAMES = {
            "Иванов", "Петров", "Сидоров", "Кузнецов", "Смирнов",
            "Соколов", "Попов", "Морозов", "Волков", "Фёдоров"
    };
    /**
     * Все возможные ранги на корабле
     */
    private static final String[] RANKS = {
            "Капитан", "Старший помощник", "Штурман",
            "Инженер", "Механик", "Бортпроводник"
    };
    /**
     * Минимальная и максимальная дата рождения
     */
    private static final LocalDate MIN_BIRTH_DATE = LocalDate.of(1960, 1, 1);
    private static final LocalDate MAX_BIRTH_DATE = LocalDate.of(2001, 1, 1);
    /**
     * Минимальный возраст с которого можно работать на корабле
     */
    private static final int MIN_WORK_YEAR_THRESHOLD = 18;

    private final Random random;

    public CrewMemberGenerator() {
        this(new Random());
    }

    public CrewMemberGenerator(Random random) {
        this.random = random;
    }

    @Override
    public CrewMember generateOne() {
        String firstName = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
        String lastName = LAST_NAMES[random.nextInt(LAST_NAMES.length)];
        String fullName = String.format("%s %s", firstName, lastName);

        String rank = RANKS[random.nextInt(RANKS.length)];

        long days = ChronoUnit.DAYS.between(MIN_BIRTH_DATE, MAX_BIRTH_DATE);
        LocalDate birthDate = MIN_BIRTH_DATE.plusDays(random.nextLong(days));

        long age = Period.between(birthDate, LocalDate.now()).getYears();
        int experienceYears = (int) Math.max(0, age - MIN_WORK_YEAR_THRESHOLD);

        return new CrewMember(fullName, rank, experienceYears, birthDate);
    }
}
