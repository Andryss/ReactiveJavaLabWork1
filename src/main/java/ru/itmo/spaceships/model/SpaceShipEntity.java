package ru.itmo.spaceships.model;

import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Космический корабль
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("spaceship")
public class SpaceShipEntity {
    /**
     * Серийный номер, уникальный для каждого корабля
     */
    @Id
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

    /**
     * Получить производителя с заданной задержкой (имитация работы)
     */
    public String getManufacturerDelayed(long delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return getManufacturer();
    }
}
