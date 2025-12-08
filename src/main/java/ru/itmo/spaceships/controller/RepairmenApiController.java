package ru.itmo.spaceships.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.itmo.spaceships.converter.RepairmanConverter;
import ru.itmo.spaceships.generated.api.RepairmenApi;
import ru.itmo.spaceships.generated.model.RepairmanDto;
import ru.itmo.spaceships.generated.model.RepairmanRequest;
import ru.itmo.spaceships.service.RepairmanService;

/**
 * Controller for repairmen API endpoints.
 */
@RestController
@RequiredArgsConstructor
public class RepairmenApiController implements RepairmenApi {

    private final RepairmanService repairmanService;
    private final RepairmanConverter repairmanConverter;

    @Override
    public Mono<RepairmanDto> createRepairman(
            Mono<RepairmanRequest> repairmanRequest,
            ServerWebExchange exchange) {
        return repairmanRequest
                .flatMap(repairmanService::createRepairman)
                .map(repairmanConverter::convertToDto);
    }

    @Override
    public Mono<RepairmanDto> updateRepairman(
            Long id,
            Mono<RepairmanRequest> repairmanRequest,
            ServerWebExchange exchange) {
        return repairmanRequest
                .flatMap(request -> repairmanService.updateRepairman(id, request))
                .map(repairmanConverter::convertToDto);
    }

    @Override
    public Mono<Void> deleteRepairman(Long id, ServerWebExchange exchange) {
        return repairmanService.deleteRepairman(id);
    }

    @Override
    public Flux<RepairmanDto> getRepairmen(
            Integer page,
            Integer size,
            ServerWebExchange exchange) {
        return repairmanService.getRepairmen(page, size)
                .map(repairmanConverter::convertToDto);
    }

    @Override
    public Mono<RepairmanDto> getRepairmanById(Long id, ServerWebExchange exchange) {
        return repairmanService.getRepairmanById(id)
                .map(repairmanConverter::convertToDto);
    }
}

