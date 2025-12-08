package ru.itmo.spaceships.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Entity representing a repairman.
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
