package ru.itmo.spaceships.controller;

import lombok.RequiredArgsConstructor;
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
 * Controller for spaceships API endpoints.
 */
@RestController
@RequiredArgsConstructor
public class SpaceShipsApiController implements SpaceshipsApi {

    private final SpaceShipService spaceShipService;
    private final SpaceShipConverter spaceShipConverter;

    @Override
    public Mono<SpaceShipDto> createSpaceship(
            Mono<SpaceShipRequest> spaceShipRequest,
            ServerWebExchange exchange) {
        return spaceShipRequest
                .flatMap(spaceShipService::createSpaceship)
                .map(spaceShipConverter::convertToDto);
    }

    @Override
    public Mono<SpaceShipDto> updateSpaceship(
            Long serial,
            Mono<SpaceShipRequest> spaceShipRequest,
            ServerWebExchange exchange) {
        return spaceShipRequest
                .flatMap(request -> spaceShipService.updateSpaceship(serial, request))
                .map(spaceShipConverter::convertToDto);
    }

    @Override
    public Mono<Void> deleteSpaceship(Long serial, ServerWebExchange exchange) {
        return spaceShipService.deleteSpaceship(serial);
    }

    @Override
    public Flux<SpaceShipDto> getSpaceships(
            Integer page,
            Integer size,
            ServerWebExchange exchange) {
        return spaceShipService.getSpaceships(page, size)
                .map(spaceShipConverter::convertToDto);
    }

    @Override
    public Mono<SpaceShipDto> getSpaceshipBySerial(Long serial, ServerWebExchange exchange) {
        return spaceShipService.getSpaceshipBySerial(serial)
                .map(spaceShipConverter::convertToDto);
    }
}

