package ru.itmo.spaceships.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.itmo.spaceships.generated.model.RepairmanRequest;
import ru.itmo.spaceships.model.RepairmanEntity;
import ru.itmo.spaceships.repository.RepairmanRepository;

/**
 * Service for working with repairmen.
 */
@Service
@RequiredArgsConstructor
public class RepairmanService {

    private final RepairmanRepository repairmanRepository;

    /**
     * Create a new repairman.
     *
     * @param request request to create repairman
     * @return created repairman entity
     */
    public Mono<RepairmanEntity> createRepairman(RepairmanRequest request) {
        if (request.getName() == null || request.getPosition() == null) {
            return Mono.error(new IllegalArgumentException("Name and position are required for creating a repairman"));
        }
        RepairmanEntity entity = new RepairmanEntity();
        entity.setName(request.getName());
        entity.setPosition(request.getPosition());

        return repairmanRepository.save(entity);
    }

    /**
     * Update an existing repairman.
     *
     * @param id repairman id
     * @param request request to update repairman
     * @return updated repairman entity
     */
    public Mono<RepairmanEntity> updateRepairman(Long id, RepairmanRequest request) {
        return repairmanRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Repairman not found with id: " + id)))
                .flatMap(entity -> {
                    if (request.getName() != null) {
                        entity.setName(request.getName());
                    }
                    if (request.getPosition() != null) {
                        entity.setPosition(request.getPosition());
                    }
                    return repairmanRepository.save(entity);
                });
    }

    /**
     * Delete a repairman.
     *
     * @param id repairman id
     * @return empty Mono
     */
    public Mono<Void> deleteRepairman(Long id) {
        return repairmanRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Repairman not found with id: " + id)))
                .flatMap(repairmanRepository::delete);
    }

    /**
     * Get all repairmen with paging.
     *
     * @param page page number (0-based)
     * @param size page size
     * @return flux of repairman entities sorted by ID
     */
    public Flux<RepairmanEntity> getRepairmen(Integer page, Integer size) {
        long offset = (long) page * size;
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        return repairmanRepository.findAll(sort)
                .skip(offset)
                .take(size);
    }

    /**
     * Get repairman by ID.
     *
     * @param id repairman id
     * @return repairman entity
     */
    public Mono<RepairmanEntity> getRepairmanById(Long id) {
        return repairmanRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Repairman not found with id: " + id)));
    }
}

