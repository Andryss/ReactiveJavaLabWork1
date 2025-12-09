package ru.itmo.spaceships.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import ru.itmo.spaceships.model.RepairmanEntity;

/**
 * Репозиторий для RepairmanEntity.
 */
public interface RepairmanRepository extends R2dbcRepository<RepairmanEntity, Long> {
}
