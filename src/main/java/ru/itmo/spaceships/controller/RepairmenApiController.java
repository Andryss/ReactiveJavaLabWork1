package ru.itmo.spaceships.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
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

    @Override
    public Mono<RepairmanDto> createRepairman(
            Mono<RepairmanRequest> repairmanRequest,
            ServerWebExchange exchange) {
        return repairmanRequest
                .flatMap(repairmanService::createRepairman);
    }

    @Override
    public Mono<RepairmanDto> updateRepairman(
            Long id,
            Mono<RepairmanRequest> repairmanRequest,
            ServerWebExchange exchange) {
        return repairmanRequest
                .flatMap(request -> repairmanService.updateRepairman(id, request));
    }

    @Override
    public Mono<Void> deleteRepairman(Long id, ServerWebExchange exchange) {
        return repairmanService.deleteRepairman(id);
    }
}

