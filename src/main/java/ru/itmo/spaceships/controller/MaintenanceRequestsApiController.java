package ru.itmo.spaceships.controller;

import lombok.RequiredArgsConstructor;
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
 * Controller for maintenance requests API endpoints.
 */
@RestController
@RequiredArgsConstructor
public class MaintenanceRequestsApiController implements MaintenanceRequestsApi {

    private final MaintenanceRequestService maintenanceRequestService;
    private final MaintenanceRequestConverter maintenanceRequestConverter;

    @Override
    public Mono<MaintenanceRequestDto> createMaintenanceRequest(
            Mono<MaintenanceRequestRequest> maintenanceRequestRequest,
            ServerWebExchange exchange) {
        return maintenanceRequestRequest
                .flatMap(maintenanceRequestService::createMaintenanceRequest)
                .map(maintenanceRequestConverter::convertToDto);
    }

    @Override
    public Mono<MaintenanceRequestDto> updateMaintenanceRequest(
            Long id,
            Mono<MaintenanceRequestRequest> maintenanceRequestRequest,
            ServerWebExchange exchange) {
        return maintenanceRequestRequest
                .flatMap(request -> maintenanceRequestService.updateMaintenanceRequest(id, request))
                .map(maintenanceRequestConverter::convertToDto);
    }

    @Override
    public Mono<Void> deleteMaintenanceRequest(Long id, ServerWebExchange exchange) {
        return maintenanceRequestService.deleteMaintenanceRequest(id);
    }

    @Override
    public Flux<MaintenanceRequestDto> getMaintenanceRequests(
            Integer page,
            Integer size,
            ServerWebExchange exchange) {
        return maintenanceRequestService.getMaintenanceRequests(page, size)
                .map(maintenanceRequestConverter::convertToDto);
    }

    @Override
    public Mono<MaintenanceRequestDto> getMaintenanceRequestById(Long id, ServerWebExchange exchange) {
        return maintenanceRequestService.getMaintenanceRequestById(id)
                .map(maintenanceRequestConverter::convertToDto);
    }
}

