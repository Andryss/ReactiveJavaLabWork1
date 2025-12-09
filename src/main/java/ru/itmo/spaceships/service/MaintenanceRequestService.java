package ru.itmo.spaceships.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.itmo.spaceships.exception.Errors;
import ru.itmo.spaceships.generated.model.MaintenanceRequestRequest;
import ru.itmo.spaceships.model.MaintenanceRequestEntity;
import ru.itmo.spaceships.model.MaintenanceStatus;
import ru.itmo.spaceships.repository.MaintenanceRequestRepository;

import java.time.Instant;

/**
 * Сервис для работы с заявками на обслуживание.
 */
@Service
@RequiredArgsConstructor
public class MaintenanceRequestService {

    private final MaintenanceRequestRepository maintenanceRequestRepository;

    /**
     * Создаёт новую заявку на обслуживание.
     * Из запроса используются только spaceshipSerial и comment.
     *
     * @param request запрос на создание заявки на обслуживание
     * @return созданная сущность заявки на обслуживание
     */
    public Mono<MaintenanceRequestEntity> createMaintenanceRequest(MaintenanceRequestRequest request) {
        if (request.getSpaceshipSerial() == null) {
            return Mono.error(Errors.maintenanceRequestSpaceshipSerialRequiredError());
        }
        if (request.getComment() == null) {
            return Mono.error(Errors.maintenanceRequestCommentRequiredError());
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
     * Обновляет существующую заявку на обслуживание.
     * Все поля могут быть обновлены, кроме createdAt и updatedAt (системные поля).
     * Переходы статусов валидируются согласно бизнес-правилам.
     *
     * @param id идентификатор заявки на обслуживание
     * @param request запрос на обновление заявки на обслуживание
     * @return обновлённая сущность заявки на обслуживание
     */
    public Mono<MaintenanceRequestEntity> updateMaintenanceRequest(Long id, MaintenanceRequestRequest request) {
        return maintenanceRequestRepository.findById(id)
                .switchIfEmpty(Mono.error(Errors.maintenanceRequestNotFound(id)))
                .flatMap(entity -> {
                    // Если статус изменяется, валидируем переход
                    if (request.getStatus() != null) {
                        MaintenanceStatus newStatus = MaintenanceStatus.valueOf(request.getStatus().name());
                        MaintenanceStatus currentStatus = entity.getStatus();
                        
                        if (currentStatus != newStatus) {
                            // Валидируем переход статуса
                            try {
                                currentStatus.validateTransition(newStatus);
                            } catch (IllegalArgumentException e) {
                                return Mono.error(Errors.maintenanceRequestStatusTransitionError(
                                        currentStatus.name(), newStatus.name()));
                            }
                            entity.setStatus(newStatus);
                        }
                    }
                    
                    // Обновляем другие поля
                    if (request.getSpaceshipSerial() != null) {
                        entity.setSpaceshipSerial(request.getSpaceshipSerial());
                    }
                    if (request.getComment() != null) {
                        entity.setComment(request.getComment());
                    }
                    if (request.getAssignee() != null) {
                        entity.setAssignee(request.getAssignee());
                    }
                    
                    // Обновляем временную метку updatedAt
                    entity.setUpdatedAt(Instant.now());
                    // createdAt и updatedAt - системные поля, не обновляются из запроса
                    return maintenanceRequestRepository.save(entity);
                });
    }

    /**
     * Удаляет заявку на обслуживание.
     *
     * @param id идентификатор заявки на обслуживание
     * @return пустой Mono
     */
    public Mono<Void> deleteMaintenanceRequest(Long id) {
        return maintenanceRequestRepository.findById(id)
                .switchIfEmpty(Mono.error(Errors.maintenanceRequestNotFound(id)))
                .flatMap(maintenanceRequestRepository::delete);
    }

    /**
     * Получает все заявки на обслуживание с пагинацией.
     *
     * @param page номер страницы (начиная с 0)
     * @param size размер страницы
     * @return поток сущностей заявок на обслуживание, отсортированных по ID
     */
    public Flux<MaintenanceRequestEntity> getMaintenanceRequests(Integer page, Integer size) {
        int pageNum = page != null ? page : 0;
        int pageSize = size != null ? size : 20;
        long offset = (long) pageNum * pageSize;
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        return maintenanceRequestRepository.findAll(sort)
                .skip(offset)
                .take(pageSize);
    }

    /**
     * Получает заявку на обслуживание по ID.
     *
     * @param id идентификатор заявки на обслуживание
     * @return сущность заявки на обслуживание
     */
    public Mono<MaintenanceRequestEntity> getMaintenanceRequestById(Long id) {
        return maintenanceRequestRepository.findById(id)
                .switchIfEmpty(Mono.error(Errors.maintenanceRequestNotFound(id)));
    }
}

