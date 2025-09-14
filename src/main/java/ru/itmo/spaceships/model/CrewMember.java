package ru.itmo.spaceships.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Член экипажа
 */
@Data
@AllArgsConstructor
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
