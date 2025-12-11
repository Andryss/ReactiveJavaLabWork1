package ru.itmo.spaceships.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Sinks;
import reactor.util.concurrent.Queues;
import ru.itmo.spaceships.model.MaintenanceRequestEntity;
import ru.itmo.spaceships.model.RepairmanEntity;
import ru.itmo.spaceships.model.SpaceShipEntity;

/**
 * Конфигурация для событий стриминга обновлений.
 * Создаёт Sinks для публикации событий обновлений различных сущностей.
 */
@Configuration
public class EventStreamingConfig {

    /**
     * Создаёт Sink для публикации событий обновления ремонтников.
     * Используется для стриминга обновлений через SSE.
     *
     * @return Sink для публикации событий обновления ремонтников
     */
    @Bean
    public Sinks.Many<RepairmanEntity> repairmanUpdateSink() {
        return Sinks.many().multicast().onBackpressureBuffer(Queues.SMALL_BUFFER_SIZE, false);
    }

    /**
     * Создаёт Sink для публикации событий обновления заявок на обслуживание.
     * Используется для стриминга обновлений через SSE.
     *
     * @return Sink для публикации событий обновления заявок на обслуживание
     */
    @Bean
    public Sinks.Many<MaintenanceRequestEntity> maintenanceRequestUpdateSink() {
        return Sinks.many().multicast().onBackpressureBuffer(Queues.SMALL_BUFFER_SIZE, false);
    }

    /**
     * Создаёт Sink для публикации событий обновления кораблей.
     * Используется для стриминга обновлений через SSE.
     *
     * @return Sink для публикации событий обновления кораблей
     */
    @Bean
    public Sinks.Many<SpaceShipEntity> spaceShipUpdateSink() {
        return Sinks.many().multicast().onBackpressureBuffer(Queues.SMALL_BUFFER_SIZE, false);
    }
}

