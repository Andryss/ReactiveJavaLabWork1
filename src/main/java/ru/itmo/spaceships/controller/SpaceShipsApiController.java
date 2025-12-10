package ru.itmo.spaceships.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.itmo.spaceships.converter.SpaceShipConverter;
import ru.itmo.spaceships.generated.api.SpaceshipsApi;
import ru.itmo.spaceships.generated.model.SpaceShipDto;
import ru.itmo.spaceships.generated.model.SpaceShipRequest;
import ru.itmo.spaceships.service.SpaceShipService;

/**
 * Контроллер для API эндпоинтов кораблей.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class SpaceShipsApiController implements SpaceshipsApi {

    private final SpaceShipService spaceShipService;
    private final SpaceShipConverter spaceShipConverter;

    @Override
    public Mono<SpaceShipDto> createSpaceship(
            Mono<SpaceShipRequest> spaceShipRequest,
            ServerWebExchange exchange) {
        log.info("POST /spaceships - Creating new spaceship");
        return spaceShipRequest
                .flatMap(request -> {
                    log.debug("Creating spaceship with serial: {}", request.getSerial());
                    return spaceShipService.createSpaceship(request);
                })
                .map(spaceShipConverter::convertToDto)
                .doOnSuccess(dto -> log.info("Successfully created spaceship with serial: {}", dto.getSerial()))
                .doOnError(error -> log.error("Error creating spaceship", error));
    }

    @Override
    public Mono<SpaceShipDto> updateSpaceship(
            Long serial,
            Mono<SpaceShipRequest> spaceShipRequest,
            ServerWebExchange exchange) {
        log.info("PUT /spaceships/{} - Updating spaceship", serial);
        return spaceShipRequest
                .flatMap(request -> spaceShipService.updateSpaceship(serial, request))
                .map(spaceShipConverter::convertToDto)
                .doOnSuccess(dto -> log.info("Successfully updated spaceship with serial: {}", dto.getSerial()))
                .doOnError(error -> log.error("Error updating spaceship with serial: {}", serial, error));
    }

    @Override
    public Mono<Void> deleteSpaceship(Long serial, ServerWebExchange exchange) {
        log.info("DELETE /spaceships/{} - Deleting spaceship", serial);
        return spaceShipService.deleteSpaceship(serial)
                .doOnSuccess(v -> log.info("Successfully deleted spaceship with serial: {}", serial))
                .doOnError(error -> log.error("Error deleting spaceship with serial: {}", serial, error));
    }

    @Override
    public Flux<SpaceShipDto> getSpaceships(
            Integer page,
            Integer size,
            ServerWebExchange exchange) {
        log.info("GET /spaceships - Listing spaceships (page={}, size={})", page, size);
        return spaceShipService.getSpaceships(page, size)
                .map(spaceShipConverter::convertToDto)
                .collectList()
                .doOnSuccess(list -> log.info("Returned {} spaceships (page={}, size={})", list.size(), page, size))
                .doOnError(error -> log.error("Error listing spaceships (page={}, size={})", page, size, error))
                .flatMapMany(Flux::fromIterable);
    }

    @Override
    public Mono<SpaceShipDto> getSpaceshipBySerial(Long serial, ServerWebExchange exchange) {
        log.info("GET /spaceships/{} - Getting spaceship by serial", serial);
        return spaceShipService.getSpaceshipBySerial(serial)
                .map(spaceShipConverter::convertToDto)
                .doOnSuccess(dto -> log.info("Successfully retrieved spaceship with serial: {}", serial))
                .doOnError(error -> log.error("Error retrieving spaceship with serial: {}", serial, error));
    }

    @Override
    public Flux<SpaceShipDto> getSpaceshipsUpdatesStream(ServerWebExchange exchange) {
        log.info("GET /spaceships/updates/stream - Starting spaceship updates stream");
        return spaceShipService.getSpaceshipsUpdatesStream()
                .map(spaceShipConverter::convertToDto)
                .doOnNext(dto -> log.info("Streaming spaceship update: serial={}", dto.getSerial()))
                .doOnError(error -> log.error("Error in spaceship updates stream", error))
                .doOnCancel(() -> log.info("Spaceship updates stream cancelled"))
                .doOnComplete(() -> log.info("Spaceship updates stream completed"));
    }
}

