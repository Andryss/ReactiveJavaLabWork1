package ru.itmo.spaceships.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.itmo.spaceships.generated.model.RepairmanDto;
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
     * @return created repairman DTO
     */
    public Mono<RepairmanDto> createRepairman(RepairmanRequest request) {
        if (request.getName() == null || request.getPosition() == null) {
            return Mono.error(new IllegalArgumentException("Name and position are required for creating a repairman"));
        }
        RepairmanEntity entity = new RepairmanEntity();
        entity.setName(request.getName());
        entity.setPosition(request.getPosition());

        return repairmanRepository.save(entity)
                .map(this::convertToDto);
    }

    /**
     * Update an existing repairman.
     *
     * @param id repairman id
     * @param request request to update repairman
     * @return updated repairman DTO
     */
    public Mono<RepairmanDto> updateRepairman(Long id, RepairmanRequest request) {
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
                })
                .map(this::convertToDto);
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

    private RepairmanDto convertToDto(RepairmanEntity entity) {
        RepairmanDto dto = new RepairmanDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setPosition(entity.getPosition());
        return dto;
    }
}

