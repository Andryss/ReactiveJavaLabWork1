package ru.itmo.spaceships.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
 * Контроллер для API эндпоинтов ремонтников.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class RepairmenApiController implements RepairmenApi {

    private final RepairmanService repairmanService;
    private final RepairmanConverter repairmanConverter;

    @Override
    public Mono<RepairmanDto> createRepairman(
            Mono<RepairmanRequest> repairmanRequest,
            ServerWebExchange exchange) {
        log.info("POST /repairmen - Creating new repairman");
        return repairmanRequest
                .flatMap(repairmanService::createRepairman)
                .map(repairmanConverter::convertToDto)
                .doOnSuccess(dto -> log.info("Successfully created repairman with id: {}", dto.getId()))
                .doOnError(error -> log.error("Error creating repairman", error));
    }

    @Override
    public Mono<RepairmanDto> updateRepairman(
            Long id,
            Mono<RepairmanRequest> repairmanRequest,
            ServerWebExchange exchange) {
        log.info("PUT /repairmen/{} - Updating repairman", id);
        return repairmanRequest
                .flatMap(request -> repairmanService.updateRepairman(id, request))
                .map(repairmanConverter::convertToDto)
                .doOnSuccess(dto -> log.info("Successfully updated repairman with id: {}", id))
                .doOnError(error -> log.error("Error updating repairman with id: {}", id, error));
    }

    @Override
    public Mono<Void> deleteRepairman(Long id, ServerWebExchange exchange) {
        log.info("DELETE /repairmen/{} - Deleting repairman", id);
        return repairmanService.deleteRepairman(id)
                .doOnSuccess(v -> log.info("Successfully deleted repairman with id: {}", id))
                .doOnError(error -> log.error("Error deleting repairman with id: {}", id, error));
    }

    @Override
    public Flux<RepairmanDto> getRepairmen(
            Integer page,
            Integer size,
            ServerWebExchange exchange) {
        log.info("GET /repairmen - Listing repairmen (page={}, size={})", page, size);
        return repairmanService.getRepairmen(page, size)
                .map(repairmanConverter::convertToDto)
                .collectList()
                .doOnSuccess(list -> log.info("Returned {} repairmen (page={}, size={})", list.size(), page, size))
                .doOnError(error -> log.error("Error listing repairmen (page={}, size={})", page, size, error))
                .flatMapMany(Flux::fromIterable);
    }

    @Override
    public Mono<RepairmanDto> getRepairmanById(Long id, ServerWebExchange exchange) {
        log.info("GET /repairmen/{} - Getting repairman by id", id);
        return repairmanService.getRepairmanById(id)
                .map(repairmanConverter::convertToDto)
                .doOnSuccess(dto -> log.info("Successfully retrieved repairman with id: {}", id))
                .doOnError(error -> log.error("Error retrieving repairman with id: {}", id, error));
    }

    @Override
    public Flux<RepairmanDto> getRepairmenUpdatesStream(ServerWebExchange exchange) {
        log.info("GET /repairmen/updates/stream - Starting repairman updates stream");
        return repairmanService.getRepairmenUpdatesStream()
                .map(repairmanConverter::convertToDto)
                .doOnNext(dto -> log.info("Streaming repairman update: id={}", dto.getId()))
                .doOnError(error -> log.error("Error in repairman updates stream", error))
                .doOnCancel(() -> log.info("Repairman updates stream cancelled"))
                .doOnComplete(() -> log.info("Repairman updates stream completed"));
    }
}

