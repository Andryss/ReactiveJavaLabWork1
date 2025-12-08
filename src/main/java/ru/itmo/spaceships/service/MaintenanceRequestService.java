package ru.itmo.spaceships.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.itmo.spaceships.generated.model.MaintenanceRequestRequest;
import ru.itmo.spaceships.model.MaintenanceRequestEntity;
import ru.itmo.spaceships.model.MaintenanceStatus;
import ru.itmo.spaceships.repository.MaintenanceRequestRepository;

import java.time.Instant;

/**
 * Service for working with maintenance requests.
 */
@Service
@RequiredArgsConstructor
public class MaintenanceRequestService {

    private final MaintenanceRequestRepository maintenanceRequestRepository;

    /**
     * Create a new maintenance request.
     * Only spaceshipSerial and comment are used from the request.
     *
     * @param request request to create maintenance request
     * @return created maintenance request entity
     */
    public Mono<MaintenanceRequestEntity> createMaintenanceRequest(MaintenanceRequestRequest request) {
        if (request.getSpaceshipSerial() == null) {
            return Mono.error(new IllegalArgumentException(
                    "Spaceship serial is required for creating a maintenance request"));
        }
        if (request.getComment() == null) {
            return Mono.error(new IllegalArgumentException("Comment is required for creating a maintenance request"));
        }
        MaintenanceRequestEntity entity = new MaintenanceRequestEntity();
        entity.setSpaceshipSerial(request.getSpaceshipSerial());
        entity.setComment(request.getComment());
        entity.setStatus(MaintenanceStatus.NEW);
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());
        return maintenanceRequestRepository.save(entity);
    }

    /**
     * Update an existing maintenance request.
     * All fields can be updated except createdAt and updatedAt (system fields).
     *
     * @param id maintenance request id
     * @param request request to update maintenance request
     * @return updated maintenance request entity
     */
    public Mono<MaintenanceRequestEntity> updateMaintenanceRequest(Long id, MaintenanceRequestRequest request) {
        return maintenanceRequestRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Maintenance request not found with id: " + id)))
                .flatMap(entity -> {
                    if (request.getSpaceshipSerial() != null) {
                        entity.setSpaceshipSerial(request.getSpaceshipSerial());
                    }
                    if (request.getComment() != null) {
                        entity.setComment(request.getComment());
                    }
                    if (request.getAssignee() != null) {
                        entity.setAssignee(request.getAssignee());
                    }
                    if (request.getStatus() != null) {
                        entity.setStatus(MaintenanceStatus.valueOf(request.getStatus().name()));
                    }
                    // Update updatedAt timestamp
                    entity.setUpdatedAt(Instant.now());
                    // createdAt and updatedAt are system fields, not updated from request
                    return maintenanceRequestRepository.save(entity);
                });
    }

    /**
     * Delete a maintenance request.
     *
     * @param id maintenance request id
     * @return empty Mono
     */
    public Mono<Void> deleteMaintenanceRequest(Long id) {
        return maintenanceRequestRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Maintenance request not found with id: " + id)))
                .flatMap(maintenanceRequestRepository::delete);
    }

    /**
     * Get all maintenance requests with paging.
     *
     * @param page page number (0-based)
     * @param size page size
     * @return flux of maintenance request entities sorted by ID
     */
    public Flux<MaintenanceRequestEntity> getMaintenanceRequests(Integer page, Integer size) {
        long offset = (long) page * size;
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        return maintenanceRequestRepository.findAll(sort)
                .skip(offset)
                .take(size);
    }

    /**
     * Get maintenance request by ID.
     *
     * @param id maintenance request id
     * @return maintenance request entity
     */
    public Mono<MaintenanceRequestEntity> getMaintenanceRequestById(Long id) {
        return maintenanceRequestRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(
                        "Maintenance request not found with id: " + id)));
    }
}

