package ru.itmo.spaceships.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import ru.itmo.spaceships.model.MaintenanceRequestEntity;

/**
 * Репозиторий для MaintenanceRequestEntity.
 */
public interface MaintenanceRequestRepository extends R2dbcRepository<MaintenanceRequestEntity, Long> {
}

