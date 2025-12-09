package ru.itmo.spaceships.converter;

import org.springframework.stereotype.Component;
import ru.itmo.spaceships.generated.model.MaintenanceRequestDto;
import ru.itmo.spaceships.model.MaintenanceRequestEntity;

import java.time.ZoneOffset;

/**
 * Конвертер для MaintenanceRequestEntity и MaintenanceRequestDto.
 */
@Component
public class MaintenanceRequestConverter {

    /**
     * Конвертирует MaintenanceRequestEntity в MaintenanceRequestDto.
     *
     * @param entity сущность заявки на обслуживание
     * @return DTO заявки на обслуживание
     */
    public MaintenanceRequestDto convertToDto(MaintenanceRequestEntity entity) {
        MaintenanceRequestDto dto = new MaintenanceRequestDto();
        dto.setId(entity.getId());
        dto.setSpaceshipSerial(entity.getSpaceshipSerial());
        dto.setComment(entity.getComment());
        dto.setCreatedAt(entity.getCreatedAt() != null
                ? entity.getCreatedAt().atOffset(ZoneOffset.UTC)
                : null);
        dto.setUpdatedAt(entity.getUpdatedAt() != null
                ? entity.getUpdatedAt().atOffset(ZoneOffset.UTC)
                : null);
        dto.setAssignee(entity.getAssignee());
        dto.setStatus(entity.getStatus() != null
                ? ru.itmo.spaceships.generated.model.MaintenanceStatus.valueOf(entity.getStatus().name())
                : null);
        return dto;
    }
}

