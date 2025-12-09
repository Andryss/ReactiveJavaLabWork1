package ru.itmo.spaceships.model;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Сущность, представляющая заявку на обслуживание.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("maintenance_request")
public class MaintenanceRequestEntity {
    @Id
    private long id;
    private long spaceshipSerial;
    private String comment;
    private Instant createdAt;
    private Instant updatedAt;
    private Long assignee;
    private MaintenanceStatus status;
}
