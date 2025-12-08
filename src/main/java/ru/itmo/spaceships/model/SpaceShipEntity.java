package ru.itmo.spaceships.model;

import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Космический корабль
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("spaceship")
public class SpaceShipEntity {
    @Id
    private long id;
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
    @Column("type")
    private SpaceShipType type;
    /**
     * ВГХ
     */
    @Column("dimensions")
    private Dimensions dimensions;
    /**
     * Двигатель
     */
    @Column("engine")
    private Engine engine;
    /**
     * Экипаж
     */
    @Column("crew")
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
