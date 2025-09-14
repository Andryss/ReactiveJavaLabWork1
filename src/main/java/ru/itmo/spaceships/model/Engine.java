package ru.itmo.spaceships.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Двигатель
 */
@Data
@AllArgsConstructor
public class Engine {
    /**
     * Модель
     */
    private String model;
    /**
     * Развиваемая тяга в кило ньютонах
     */
    private int thrust;
    /**
     * Тип потребляемого топлива
     */
    private FuelType fuelType;
    /**
     * Количество потребляемого топлива в час (зависит от типа топлива)
     */
    private double fuelConsumption;
}
