package ru.itmo.spaceships.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Sinks;
import ru.itmo.spaceships.model.RepairmanEntity;

/**
 * Конфигурация для событий обновления ремонтников.
 * Создаёт Sink для публикации событий обновления ремонтников.
 */
@Configuration
public class RepairmanUpdateEventConfig {

    /**
     * Создаёт Sink для публикации событий обновления ремонтников.
     * Используется для стриминга обновлений через SSE.
     *
     * @return Sink для публикации событий обновления ремонтников
     */
    @Bean
    public Sinks.Many<RepairmanEntity> repairmanUpdateSink() {
        return Sinks.many().multicast().onBackpressureBuffer();
    }
}

