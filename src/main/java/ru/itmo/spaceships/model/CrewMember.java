package ru.itmo.spaceships.model;

import java.time.LocalDate;

/**
 * Член экипажа
 */
public class CrewMember {
    /**
     * ФИО
     */
    private String fullName;
    /**
     * Должность или ранг
     */
    private String rank;
    /**
     * Опыт работы в полных годах
     */
    private int experienceYears;
    /**
     * Дата рождения
     */
    private LocalDate birthDate;
}
