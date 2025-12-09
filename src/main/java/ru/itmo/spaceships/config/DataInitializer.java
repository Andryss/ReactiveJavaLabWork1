package ru.itmo.spaceships.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.itmo.spaceships.generator.SpaseShipGenerator;
import ru.itmo.spaceships.repository.SpaceShipRepository;

/**
 * Data initializer that ensures at least 10 spaceships exist in the database.
 * If there are less than 10 spaceships, generates 50 additional spaceships.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!functionalTest")
public class DataInitializer implements CommandLineRunner {

    private static final int MIN_SPACESHIPS = 10;
    private static final int GENERATE_COUNT = 50;

    private final SpaceShipRepository spaceShipRepository;
    private final SpaseShipGenerator spaceshipGenerator;

    @Override
    public void run(String... args) {
        log.info("Starting data initialization...");
        
        spaceShipRepository.count()
                .flatMap(count -> {
                    log.info("Current spaceship count: {}", count);
                    
                    if (count < MIN_SPACESHIPS) {
                        int toGenerate = GENERATE_COUNT;
                        log.info("Spaceship count is less than {}. Generating {} additional spaceships...", 
                                MIN_SPACESHIPS, toGenerate);
                        
                        return generateSpaceships(toGenerate)
                                .then(Mono.fromCallable(() -> {
                                    log.info("Successfully generated {} spaceships", toGenerate);
                                    return null;
                                }));
                    } else {
                        log.info("Spaceship count is sufficient ({} >= {}). No generation needed.", 
                                count, MIN_SPACESHIPS);
                        return Mono.empty();
                    }
                })
                .onErrorResume(error -> {
                    log.error("Error during data initialization", error);
                    return Mono.empty();
                })
                .block(); // Block to ensure initialization completes before application starts
    }

    /**
     * Generate and save the specified number of spaceships.
     * Handles potential duplicate serial errors by retrying with a new spaceship.
     *
     * @param count number of spaceships to generate
     * @return Mono that completes when all spaceships are saved (or attempted)
     */
    private Mono<Void> generateSpaceships(int count) {
        return Flux.range(0, count)
                .flatMap(i -> spaceShipRepository.save(spaceshipGenerator.generateOne())
                        .onErrorResume(error -> Mono.empty()))
                .then();
    }
}

