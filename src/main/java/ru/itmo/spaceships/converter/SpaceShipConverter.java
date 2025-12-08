package ru.itmo.spaceships.converter;

import org.springframework.stereotype.Component;
import ru.itmo.spaceships.generated.model.CrewMemberDto;
import ru.itmo.spaceships.generated.model.DimensionsDto;
import ru.itmo.spaceships.generated.model.EngineDto;
import ru.itmo.spaceships.generated.model.SpaceShipDto;
import ru.itmo.spaceships.generated.model.SpaceShipRequest;
import ru.itmo.spaceships.model.CrewMember;
import ru.itmo.spaceships.model.Dimensions;
import ru.itmo.spaceships.model.Engine;
import ru.itmo.spaceships.model.FuelType;
import ru.itmo.spaceships.model.SpaceShipEntity;
import ru.itmo.spaceships.model.SpaceShipType;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Converter for SpaceShipEntity and SpaceShipDto.
 */
@Component
public class SpaceShipConverter {

    /**
     * Convert SpaceShipEntity to SpaceShipDto.
     *
     * @param entity spaceship entity
     * @return spaceship DTO
     */
    public SpaceShipDto convertToDto(SpaceShipEntity entity) {
        SpaceShipDto dto = new SpaceShipDto();
        dto.setSerial(entity.getSerial());
        dto.setManufacturer(entity.getManufacturer());
        dto.setManufactureDate(entity.getManufactureDate() != null
                ? entity.getManufactureDate().atOffset(ZoneOffset.UTC)
                : null);
        dto.setName(entity.getName());
        dto.setType(entity.getType() != null
                ? ru.itmo.spaceships.generated.model.SpaceShipType.valueOf(entity.getType().name())
                : null);
        dto.setDimensions(convertDimensionsToDto(entity.getDimensions()));
        dto.setEngine(convertEngineToDto(entity.getEngine()));
        dto.setCrew(convertCrewToDto(entity.getCrew()));
        dto.setMaxSpeed(entity.getMaxSpeed());
        return dto;
    }

    /**
     * Convert SpaceShipRequest to SpaceShipEntity.
     *
     * @param request spaceship request
     * @return spaceship entity
     */
    public SpaceShipEntity convertToEntity(SpaceShipRequest request) {
        SpaceShipEntity entity = new SpaceShipEntity();
        // Serial will be set by service layer based on context (create vs update)
        if (request.getSerial() != null) {
            entity.setSerial(request.getSerial());
        }
        entity.setManufacturer(request.getManufacturer());
        entity.setManufactureDate(request.getManufactureDate() != null
                ? request.getManufactureDate().toInstant()
                : null);
        entity.setName(request.getName());
        entity.setType(request.getType() != null
                ? SpaceShipType.valueOf(request.getType().name())
                : null);
        entity.setDimensions(convertDimensionsToEntity(request.getDimensions()));
        entity.setEngine(convertEngineToEntity(request.getEngine()));
        entity.setCrew(convertCrewToEntity(request.getCrew()));
        entity.setMaxSpeed(request.getMaxSpeed() != null ? request.getMaxSpeed() : 0);
        return entity;
    }

    private DimensionsDto convertDimensionsToDto(Dimensions model) {
        if (model == null) {
            return null;
        }
        DimensionsDto dto = new DimensionsDto();
        dto.setLength(model.length());
        dto.setWidth(model.width());
        dto.setHeight(model.height());
        dto.setWeight(model.weight());
        dto.setVolume(model.volume());
        return dto;
    }

    private Dimensions convertDimensionsToEntity(DimensionsDto dto) {
        if (dto == null) {
            return null;
        }
        return new Dimensions(
                dto.getLength() != null ? dto.getLength() : 0L,
                dto.getWidth() != null ? dto.getWidth() : 0L,
                dto.getHeight() != null ? dto.getHeight() : 0L,
                dto.getWeight() != null ? dto.getWeight() : 0.0,
                dto.getVolume() != null ? dto.getVolume() : 0.0
        );
    }

    private EngineDto convertEngineToDto(Engine model) {
        if (model == null) {
            return null;
        }
        EngineDto dto = new EngineDto();
        dto.setModel(model.getModel());
        dto.setThrust(model.getThrust());
        dto.setFuelType(model.getFuelType() != null
                ? ru.itmo.spaceships.generated.model.FuelType.valueOf(model.getFuelType().name())
                : null);
        dto.setFuelConsumption(model.getFuelConsumption());
        return dto;
    }

    private Engine convertEngineToEntity(EngineDto dto) {
        if (dto == null) {
            return null;
        }
        return new Engine(
                dto.getModel(),
                dto.getThrust() != null ? dto.getThrust() : 0,
                dto.getFuelType() != null
                        ? FuelType.valueOf(dto.getFuelType().name())
                        : null,
                dto.getFuelConsumption() != null ? dto.getFuelConsumption() : 0.0
        );
    }

    private List<CrewMemberDto> convertCrewToDto(List<CrewMember> model) {
        if (model == null) {
            return new ArrayList<>();
        }
        return model.stream()
                .map(this::convertCrewMemberToDto)
                .collect(Collectors.toList());
    }

    private CrewMemberDto convertCrewMemberToDto(CrewMember model) {
        CrewMemberDto dto = new CrewMemberDto();
        dto.setFullName(model.getFullName());
        dto.setRank(model.getRank());
        dto.setExperienceYears(model.getExperienceYears());
        dto.setBirthDate(model.getBirthDate());
        return dto;
    }

    private List<CrewMember> convertCrewToEntity(List<CrewMemberDto> dto) {
        if (dto == null) {
            return new ArrayList<>();
        }
        return dto.stream()
                .map(this::convertCrewMemberToEntity)
                .collect(Collectors.toList());
    }

    private CrewMember convertCrewMemberToEntity(CrewMemberDto dto) {
        return new CrewMember(
                dto.getFullName(),
                dto.getRank(),
                dto.getExperienceYears() != null ? dto.getExperienceYears() : 0,
                dto.getBirthDate()
        );
    }
}
