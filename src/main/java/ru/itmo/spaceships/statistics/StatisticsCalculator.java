package ru.itmo.spaceships.statistics;

import java.util.List;

/**
 * Класс для сбора какой-то статистики со списка объектов
 * @param <T> тип обрабатываемых объектов
 * @param <R> тип статистики (число или комплексный объект)
 */
public interface StatisticsCalculator<T, R> {
    /**
     * Рассчитать статистику на заданном списке объектов
     */
    R calculate(List<T> objects);
}
