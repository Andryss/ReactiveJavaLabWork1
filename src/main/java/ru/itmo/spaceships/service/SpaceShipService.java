package ru.itmo.spaceships.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.itmo.spaceships.converter.SpaceShipConverter;
import ru.itmo.spaceships.exception.Errors;
import ru.itmo.spaceships.generated.model.SpaceShipRequest;
import ru.itmo.spaceships.model.SpaceShipEntity;
import ru.itmo.spaceships.repository.SpaceShipRepository;

/**
 * Сервис для работы с кораблями.
 */
@Service
@RequiredArgsConstructor
public class SpaceShipService {

    private final SpaceShipRepository spaceShipRepository;
    private final SpaceShipConverter spaceShipConverter;

    /**
     * Создаёт новый корабль.
     *
     * @param request запрос на создание корабля
     * @return созданная сущность корабля
     */
    public Mono<SpaceShipEntity> createSpaceship(SpaceShipRequest request) {
        if (request.getSerial() == null) {
            return Mono.error(Errors.spaceshipSerialRequiredError());
        }
        if (request.getManufacturer() == null || request.getName() == null
                || request.getManufactureDate() == null || request.getType() == null) {
            return Mono.error(Errors.spaceshipRequiredFieldsError());
        }
        SpaceShipEntity entity = spaceShipConverter.convertToEntity(request);
        entity.setSerial(request.getSerial());
        return spaceShipRepository.save(entity);
    }

    /**
     * Обновляет существующий корабль.
     *
     * @param serial серийный номер корабля из параметра пути
     * @param request запрос на обновление корабля (поле serial игнорируется)
     * @return обновлённая сущность корабля
     */
    public Mono<SpaceShipEntity> updateSpaceship(Long serial, SpaceShipRequest request) {
        return spaceShipRepository.findBySerial(serial)
                .switchIfEmpty(Mono.error(Errors.spaceshipNotFound(serial)))
                .flatMap(entity -> {
                    SpaceShipEntity updated = spaceShipConverter.convertToEntity(request);
                    // Сохраняем id и serial из существующей сущности
                    updated.setId(entity.getId());
                    updated.setSerial(serial);
                    return spaceShipRepository.save(updated);
                });
    }

    /**
     * Удаляет корабль.
     *
     * @param serial серийный номер корабля
     * @return пустой Mono
     */
    public Mono<Void> deleteSpaceship(Long serial) {
        return spaceShipRepository.findBySerial(serial)
                .switchIfEmpty(Mono.error(Errors.spaceshipNotFound(serial)))
                .flatMap(spaceShipRepository::delete);
    }

    /**
     * Получает все корабли с пагинацией.
     *
     * @param page номер страницы (начиная с 0)
     * @param size размер страницы
     * @return поток сущностей кораблей, отсортированных по серийному номеру
     */
    public Flux<SpaceShipEntity> getSpaceships(Integer page, Integer size) {
        int pageNum = page != null ? page : 0;
        int pageSize = size != null ? size : 20;
        long offset = (long) pageNum * pageSize;
        Sort sort = Sort.by(Sort.Direction.ASC, "serial");
        return spaceShipRepository.findAll(sort)
                .skip(offset)
                .take(pageSize);
    }

    /**
     * Получает корабль по серийному номеру.
     *
     * @param serial серийный номер корабля
     * @return сущность корабля
     */
    public Mono<SpaceShipEntity> getSpaceshipBySerial(Long serial) {
        return spaceShipRepository.findBySerial(serial)
                .switchIfEmpty(Mono.error(Errors.spaceshipNotFound(serial)));
    }
}

