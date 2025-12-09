package ru.itmo.spaceships.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.itmo.spaceships.generator.RepairmanGenerator;
import ru.itmo.spaceships.generator.SpaseShipGenerator;
import ru.itmo.spaceships.repository.RepairmanRepository;
import ru.itmo.spaceships.repository.SpaceShipRepository;

/**
 * Инициализатор данных, который обеспечивает наличие минимально необходимых данных в базе данных.
 * - Обеспечивает наличие минимум 10 кораблей (генерирует 50, если меньше)
 * - Обеспечивает наличие минимум 10 ремонтников (генерирует 10, если меньше)
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!functionalTest")
public class DataInitializer implements CommandLineRunner {

    private static final int MIN_SPACESHIPS = 10;
    private static final int GENERATE_SPACESHIPS_COUNT = 50;
    private static final int MIN_REPAIRMEN = 10;
    private static final int GENERATE_REPAIRMEN_COUNT = 10;

    private final SpaceShipRepository spaceShipRepository;
    private final RepairmanRepository repairmanRepository;
    private final SpaseShipGenerator spaceshipGenerator;
    private final RepairmanGenerator repairmanGenerator;

    @Override
    public void run(String... args) {
        log.info("Запуск инициализации данных...");

        // Инициализация кораблей
        spaceShipRepository.count()
                .flatMap(count -> {
                    log.info("Current spaceship count: {}", count);

                    if (count < MIN_SPACESHIPS) {
                        int toGenerate = GENERATE_SPACESHIPS_COUNT;
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
                .then(repairmanRepository.count()
                        .flatMap(count -> {
                            log.info("Current repairman count: {}", count);

                            if (count < MIN_REPAIRMEN) {
                                int toGenerate = GENERATE_REPAIRMEN_COUNT;
                                log.info("Repairman count is less than {}. Generating {} additional repairmen...",
                                        MIN_REPAIRMEN, toGenerate);

                                return generateRepairmen(toGenerate)
                                        .then(Mono.fromCallable(() -> {
                                            log.info("Successfully generated {} repairmen", toGenerate);
                                            return null;
                                        }));
                            } else {
                                log.info("Repairman count is sufficient ({} >= {}). No generation needed.",
                                        count, MIN_REPAIRMEN);
                                return Mono.empty();
                            }
                        }))
                .onErrorResume(error -> {
                    log.error("Ошибка при инициализации данных", error);
                    return Mono.empty();
                })
                .block(); // Блокируем выполнение, чтобы инициализация завершилась до запуска приложения
    }

    /**
     * Генерирует и сохраняет указанное количество кораблей.
     * Обрабатывает возможные ошибки дублирования серийных номеров, пропуская неудачные сохранения.
     *
     * @param count количество кораблей для генерации
     * @return Mono, который завершается, когда все корабли сохранены (или попытка сохранения выполнена)
     */
    private Mono<Void> generateSpaceships(int count) {
        return Flux.range(0, count)
                .flatMap(i -> spaceShipRepository.save(spaceshipGenerator.generateOne())
                        .onErrorResume(error -> Mono.empty()))
                .then();
    }

    /**
     * Генерирует и сохраняет указанное количество ремонтников.
     *
     * @param count количество ремонтников для генерации
     * @return Mono, который завершается, когда все ремонтники сохранены (или попытка сохранения выполнена)
     */
    private Mono<Void> generateRepairmen(int count) {
        return Flux.range(0, count)
                .flatMap(i -> repairmanRepository.save(repairmanGenerator.generateOne())
                        .onErrorResume(error -> Mono.empty()))
                .then();
    }
}

