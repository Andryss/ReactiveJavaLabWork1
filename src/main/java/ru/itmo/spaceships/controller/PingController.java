package ru.itmo.spaceships.controller;

import java.time.Duration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
public class PingController {
    @GetMapping("/ping")
    public Mono<String> ping() {
        log.info("Endpoint /ping called");
        return Mono.just("pong");
    }

    @GetMapping("/pinger")
    public Flux<String> pinger() {
        return Flux.interval(Duration.ofSeconds(3))
                .map(i -> "pong");
    }
}
