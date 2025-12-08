package ru.itmo.spaceships.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;
import ru.itmo.spaceships.model.SpaceShipEntity;

/**
 * Repository for SpaceShipEntity.
 */
public interface SpaceShipRepository extends R2dbcRepository<SpaceShipEntity, Long> {

    /**
     * Find spaceship by serial number.
     *
     * @param serial serial number
     * @return spaceship entity
     */
    Mono<SpaceShipEntity> findBySerial(Long serial);
}

