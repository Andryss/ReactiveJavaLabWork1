package ru.itmo.spaceships.model;

import java.time.Instant;
import java.util.List;

/**
 * Космический корабль
 */
public class SpaceShip {
    /**
     * Серийный номер, уникальный для каждого корабля
     */
    private long serial;
    /**
     * Название компании изготовителя
     */
    private String manufacturer;
    /**
     * Дата и время изготовления
     */
    private Instant manufactureDate;
    /**
     * Название
     */
    private String name;
    /**
     * Тип корабля
     */
    private SpaceShipType type;
    /**
     * ВГХ
     */
    private Dimensions dimensions;
    /**
     * Двигатель
     */
    private Engine engine;
    /**
     * Экипаж
     */
    private List<CrewMember> crew;
    /**
     * Максимальная развиваемая скорость
     */
    private int maxSpeed;
}
