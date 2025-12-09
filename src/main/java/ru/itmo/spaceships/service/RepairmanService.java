package ru.itmo.spaceships.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.itmo.spaceships.exception.Errors;
import ru.itmo.spaceships.generated.model.RepairmanRequest;
import ru.itmo.spaceships.model.RepairmanEntity;
import ru.itmo.spaceships.repository.RepairmanRepository;

/**
 * Сервис для работы с ремонтниками.
 */
@Service
@RequiredArgsConstructor
public class RepairmanService {

    private final RepairmanRepository repairmanRepository;

    /**
     * Создаёт нового ремонтника.
     *
     * @param request запрос на создание ремонтника
     * @return созданная сущность ремонтника
     */
    public Mono<RepairmanEntity> createRepairman(RepairmanRequest request) {
        if (request.getName() == null || request.getPosition() == null) {
            return Mono.error(Errors.repairmanValidationError());
        }
        RepairmanEntity entity = new RepairmanEntity();
        entity.setName(request.getName());
        entity.setPosition(request.getPosition());

        return repairmanRepository.save(entity);
    }

    /**
     * Обновляет существующего ремонтника.
     *
     * @param id идентификатор ремонтника
     * @param request запрос на обновление ремонтника
     * @return обновлённая сущность ремонтника
     */
    public Mono<RepairmanEntity> updateRepairman(Long id, RepairmanRequest request) {
        return repairmanRepository.findById(id)
                .switchIfEmpty(Mono.error(Errors.repairmanNotFound(id)))
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
     * Удаляет ремонтника.
     *
     * @param id идентификатор ремонтника
     * @return пустой Mono
     */
    public Mono<Void> deleteRepairman(Long id) {
        return repairmanRepository.findById(id)
                .switchIfEmpty(Mono.error(Errors.repairmanNotFound(id)))
                .flatMap(repairmanRepository::delete);
    }

    /**
     * Получает всех ремонтников с пагинацией.
     *
     * @param page номер страницы (начиная с 0)
     * @param size размер страницы
     * @return поток сущностей ремонтников, отсортированных по ID
     */
    public Flux<RepairmanEntity> getRepairmen(Integer page, Integer size) {
        int pageNum = page != null ? page : 0;
        int pageSize = size != null ? size : 20;
        long offset = (long) pageNum * pageSize;
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        return repairmanRepository.findAll(sort)
                .skip(offset)
                .take(pageSize);
    }

    /**
     * Получает ремонтника по ID.
     *
     * @param id идентификатор ремонтника
     * @return сущность ремонтника
     */
    public Mono<RepairmanEntity> getRepairmanById(Long id) {
        return repairmanRepository.findById(id)
                .switchIfEmpty(Mono.error(Errors.repairmanNotFound(id)));
    }
}

