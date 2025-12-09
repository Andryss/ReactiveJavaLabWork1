package ru.itmo.spaceships.converter;

import org.springframework.stereotype.Component;
import ru.itmo.spaceships.generated.model.RepairmanDto;
import ru.itmo.spaceships.model.RepairmanEntity;

/**
 * Конвертер для RepairmanEntity и RepairmanDto.
 */
@Component
public class RepairmanConverter {

    /**
     * Конвертирует RepairmanEntity в RepairmanDto.
     *
     * @param entity сущность ремонтника
     * @return DTO ремонтника
     */
    public RepairmanDto convertToDto(RepairmanEntity entity) {
        RepairmanDto dto = new RepairmanDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setPosition(entity.getPosition());
        return dto;
    }
}

