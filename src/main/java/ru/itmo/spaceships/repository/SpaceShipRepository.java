package ru.itmo.spaceships.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;
import ru.itmo.spaceships.model.SpaceShipEntity;

/**
 * Репозиторий для SpaceShipEntity.
 */
public interface SpaceShipRepository extends R2dbcRepository<SpaceShipEntity, Long> {

    /**
     * Находит корабль по серийному номеру.
     *
     * @param serial серийный номер
     * @return сущность корабля
     */
    Mono<SpaceShipEntity> findBySerial(Long serial);
}

