package ru.itmo.spaceships.converter;

import org.springframework.stereotype.Component;
import ru.itmo.spaceships.generated.model.RepairmanDto;
import ru.itmo.spaceships.model.RepairmanEntity;

/**
 * Converter for RepairmanEntity and RepairmanDto.
 */
@Component
public class RepairmanConverter {

    /**
     * Convert RepairmanEntity to RepairmanDto.
     *
     * @param entity repairman entity
     * @return repairman DTO
     */
    public RepairmanDto convertToDto(RepairmanEntity entity) {
        RepairmanDto dto = new RepairmanDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setPosition(entity.getPosition());
        return dto;
    }
}

