package ru.itmo.spaceships.model;

/**
 * ВГХ
 * @param length длина в метрах
 * @param width ширина в метрах
 * @param height высота в метрах
 * @param weight масса в тоннах
 * @param volume объем в кубических метрах
 */
public record Dimensions(long length, long width, long height, double weight, double volume) {
}
