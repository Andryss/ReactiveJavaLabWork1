package ru.itmo.spaceships.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.itmo.spaceships.converter.SpaceShipConverter;
import ru.itmo.spaceships.generated.model.SpaceShipRequest;
import ru.itmo.spaceships.model.SpaceShipEntity;
import ru.itmo.spaceships.repository.SpaceShipRepository;

/**
 * Service for working with spaceships.
 */
@Service
@RequiredArgsConstructor
public class SpaceShipService {

    private final SpaceShipRepository spaceShipRepository;
    private final SpaceShipConverter spaceShipConverter;

    /**
     * Create a new spaceship.
     *
     * @param request request to create spaceship
     * @return created spaceship entity
     */
    public Mono<SpaceShipEntity> createSpaceship(SpaceShipRequest request) {
        if (request.getSerial() == null) {
            return Mono.error(new IllegalArgumentException("Serial is required for creating a spaceship"));
        }
        if (request.getManufacturer() == null || request.getName() == null
                || request.getManufactureDate() == null || request.getType() == null) {
            return Mono.error(new IllegalArgumentException(
                    "Manufacturer, name, manufactureDate, and type are required for creating a spaceship"));
        }
        SpaceShipEntity entity = spaceShipConverter.convertToEntity(request);
        entity.setSerial(request.getSerial());
        return spaceShipRepository.save(entity);
    }

    /**
     * Update an existing spaceship.
     *
     * @param serial spaceship serial number from path parameter
     * @param request request to update spaceship (serial field is ignored)
     * @return updated spaceship entity
     */
    public Mono<SpaceShipEntity> updateSpaceship(Long serial, SpaceShipRequest request) {
        return spaceShipRepository.findBySerial(serial)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Spaceship not found with serial: " + serial)))
                .flatMap(entity -> {
                    SpaceShipEntity updated = spaceShipConverter.convertToEntity(request);
                    // Preserve id and serial from existing entity
                    updated.setId(entity.getId());
                    updated.setSerial(serial);
                    return spaceShipRepository.save(updated);
                });
    }

    /**
     * Delete a spaceship.
     *
     * @param serial spaceship serial number
     * @return empty Mono
     */
    public Mono<Void> deleteSpaceship(Long serial) {
        return spaceShipRepository.findBySerial(serial)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Spaceship not found with serial: " + serial)))
                .flatMap(spaceShipRepository::delete);
    }

    /**
     * Get all spaceships with paging.
     *
     * @param page page number (0-based)
     * @param size page size
     * @return flux of spaceship entities sorted by serial
     */
    public Flux<SpaceShipEntity> getSpaceships(Integer page, Integer size) {
        long offset = (long) page * size;
        Sort sort = Sort.by(Sort.Direction.ASC, "serial");
        return spaceShipRepository.findAll(sort)
                .skip(offset)
                .take(size);
    }

    /**
     * Get spaceship by serial number.
     *
     * @param serial spaceship serial number
     * @return spaceship entity
     */
    public Mono<SpaceShipEntity> getSpaceshipBySerial(Long serial) {
        return spaceShipRepository.findBySerial(serial)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Spaceship not found with serial: " + serial)));
    }
}

