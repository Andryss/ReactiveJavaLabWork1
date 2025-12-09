package ru.itmo.spaceships.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Сущность, представляющая ремонтника.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("repairmen")
public class RepairmanEntity {
    @Id
    private long id;
    private String name;
    private String position;
}
