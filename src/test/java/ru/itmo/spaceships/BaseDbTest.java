package ru.itmo.spaceships;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.itmo.spaceships.config.R2dbcTestConfig;
import ru.itmo.spaceships.repository.MaintenanceRequestRepository;
import ru.itmo.spaceships.repository.RepairmanRepository;
import ru.itmo.spaceships.repository.SpaceShipRepository;

/**
 * Base test class for database tests.
 * Provides automatic cleanup of all repositories after each test.
 */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("functionalTest")
@Import(R2dbcTestConfig.class)
public abstract class BaseDbTest {

    @Autowired
    private SpaceShipRepository spaceShipRepository;

    @Autowired
    private RepairmanRepository repairmanRepository;

    @Autowired
    private MaintenanceRequestRepository maintenanceRequestRepository;

    /**
     * Clean all repositories after each test to ensure test isolation.
     * Deletes all entities in the correct order to respect foreign key constraints.
     */
    @AfterEach
    void cleanRepositories() {
        log.debug("Cleaning all repositories after test");
        // Delete all entities from all repositories
        // Order matters: delete maintenance requests first (they reference spaceships and repairmen)
        maintenanceRequestRepository.deleteAll()
                .then(spaceShipRepository.deleteAll())
                .then(repairmanRepository.deleteAll())
                .doOnSuccess(v -> log.debug("Successfully cleaned all repositories"))
                .doOnError(error -> log.error("Error cleaning repositories", error))
                .block(); // Block to ensure cleanup completes before next test
    }
}
