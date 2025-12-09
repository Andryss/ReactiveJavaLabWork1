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
 * Базовый класс для тестов базы данных.
 * Обеспечивает автоматическую очистку всех репозиториев после каждого теста.
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
     * Очищает все репозитории после каждого теста для обеспечения изоляции тестов.
     * Удаляет все сущности в правильном порядке с учётом ограничений внешних ключей.
     */
    @AfterEach
    void cleanRepositories() {
        log.debug("Очистка всех репозиториев после теста");
        // Удаление всех сущностей из всех репозиториев
        // Порядок важен: сначала удаляем заявки на обслуживание (они ссылаются на корабли и ремонтников)
        maintenanceRequestRepository.deleteAll()
                .then(spaceShipRepository.deleteAll())
                .then(repairmanRepository.deleteAll())
                .doOnSuccess(v -> log.debug("Все репозитории успешно очищены"))
                .doOnError(error -> log.error("Ошибка при очистке репозиториев", error))
                .block(); // Блокируем выполнение, чтобы очистка завершилась до следующего теста
    }
}
