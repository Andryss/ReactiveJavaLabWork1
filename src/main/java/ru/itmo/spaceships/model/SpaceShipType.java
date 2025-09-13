package ru.itmo.spaceships.model;

/**
 * Тип корабля (определяет тип деятельности и внутреннее и внешнее наполнение)
 */
public enum SpaceShipType {
    /**
     * Грузовой
     */
    CARGO,
    /**
     * Исследовательский
     */
    EXPLORATION,
    /**
     * Разведывательный
     */
    SCOUT,
    /**
     * Боевой
     */
    BATTLE,
    /**
     * Пассажирский
     */
    PASSENGER
}
