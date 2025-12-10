package ru.itmo.spaceships.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.itmo.spaceships.converter.MaintenanceRequestConverter;
import ru.itmo.spaceships.generated.api.MaintenanceRequestsApi;
import ru.itmo.spaceships.generated.model.MaintenanceRequestDto;
import ru.itmo.spaceships.generated.model.MaintenanceRequestRequest;
import ru.itmo.spaceships.service.MaintenanceRequestService;

/**
 * Контроллер для API эндпоинтов заявок на обслуживание.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class MaintenanceRequestsApiController implements MaintenanceRequestsApi {

    private final MaintenanceRequestService maintenanceRequestService;
    private final MaintenanceRequestConverter maintenanceRequestConverter;

    @Override
    public Mono<MaintenanceRequestDto> createMaintenanceRequest(
            Mono<MaintenanceRequestRequest> maintenanceRequestRequest,
            ServerWebExchange exchange) {
        log.info("POST /maintenance-requests - Creating new maintenance request");
        return maintenanceRequestRequest
                .flatMap(maintenanceRequestService::createMaintenanceRequest)
                .map(maintenanceRequestConverter::convertToDto)
                .doOnSuccess(dto -> log.info("Successfully created maintenance request with id: {}", dto.getId()))
                .doOnError(error -> log.error("Error creating maintenance request", error));
    }

    @Override
    public Mono<MaintenanceRequestDto> updateMaintenanceRequest(
            Long id,
            Mono<MaintenanceRequestRequest> maintenanceRequestRequest,
            ServerWebExchange exchange) {
        log.info("PUT /maintenance-requests/{} - Updating maintenance request", id);
        return maintenanceRequestRequest
                .flatMap(request -> maintenanceRequestService.updateMaintenanceRequest(id, request))
                .map(maintenanceRequestConverter::convertToDto)
                .doOnSuccess(dto -> log.info("Successfully updated maintenance request with id: {}", id))
                .doOnError(error -> log.error("Error updating maintenance request with id: {}", id, error));
    }

    @Override
    public Mono<Void> deleteMaintenanceRequest(Long id, ServerWebExchange exchange) {
        log.info("DELETE /maintenance-requests/{} - Deleting maintenance request", id);
        return maintenanceRequestService.deleteMaintenanceRequest(id)
                .doOnSuccess(v -> log.info("Successfully deleted maintenance request with id: {}", id))
                .doOnError(error -> log.error("Error deleting maintenance request with id: {}", id, error));
    }

    @Override
    public Flux<MaintenanceRequestDto> getMaintenanceRequests(
            Integer page,
            Integer size,
            ServerWebExchange exchange) {
        log.info("GET /maintenance-requests - Listing maintenance requests (page={}, size={})", page, size);
        return maintenanceRequestService.getMaintenanceRequests(page, size)
                .map(maintenanceRequestConverter::convertToDto)
                .collectList()
                .doOnSuccess(list -> log.info("Returned {} maintenance requests (page={}, size={})",
                        list.size(), page, size))
                .doOnError(error -> log.error("Error listing maintenance requests (page={}, size={})",
                        page, size, error))
                .flatMapMany(Flux::fromIterable);
    }

    @Override
    public Mono<MaintenanceRequestDto> getMaintenanceRequestById(Long id, ServerWebExchange exchange) {
        log.info("GET /maintenance-requests/{} - Getting maintenance request by id", id);
        return maintenanceRequestService.getMaintenanceRequestById(id)
                .map(maintenanceRequestConverter::convertToDto)
                .doOnSuccess(dto -> log.info("Successfully retrieved maintenance request with id: {}", id))
                .doOnError(error -> log.error("Error retrieving maintenance request with id: {}", id, error));
    }

    @Override
    public Flux<MaintenanceRequestDto> getMaintenanceRequestsUpdatesStream(ServerWebExchange exchange) {
        log.info("GET /maintenance-requests/updates/stream - Starting maintenance request updates stream");
        return maintenanceRequestService.getMaintenanceRequestsUpdatesStream()
                .map(maintenanceRequestConverter::convertToDto)
                .doOnNext(dto -> log.info("Streaming maintenance request update: id={}", dto.getId()))
                .doOnError(error -> log.error("Error in maintenance request updates stream", error))
                .doOnCancel(() -> log.info("Maintenance request updates stream cancelled"))
                .doOnComplete(() -> log.info("Maintenance request updates stream completed"));
    }
}

