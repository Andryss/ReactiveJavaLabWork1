package ru.itmo.spaceships.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.itmo.spaceships.generator.CrewMemberGenerator;
import ru.itmo.spaceships.generator.DimensionsGenerator;
import ru.itmo.spaceships.generator.EngineGenerator;
import ru.itmo.spaceships.generator.RepairmanGenerator;
import ru.itmo.spaceships.generator.SpaseShipGenerator;

import java.util.Random;

/**
 * Конфигурация для бинов генераторов.
 */
@Configuration
public class GeneratorConfig {

    /**
     * Создаёт бин для генератора случайных чисел.
     *
     * @return экземпляр Random
     */
    @Bean
    public Random random() {
        return new Random();
    }

    /**
     * Создаёт бин для DimensionsGenerator.
     *
     * @param random генератор случайных чисел
     * @return экземпляр DimensionsGenerator
     */
    @Bean
    public DimensionsGenerator dimensionsGenerator(Random random) {
        return new DimensionsGenerator(random);
    }

    /**
     * Создаёт бин для EngineGenerator.
     *
     * @param random генератор случайных чисел
     * @return экземпляр EngineGenerator
     */
    @Bean
    public EngineGenerator engineGenerator(Random random) {
        return new EngineGenerator(random);
    }

    /**
     * Создаёт бин для CrewMemberGenerator.
     *
     * @param random генератор случайных чисел
     * @return экземпляр CrewMemberGenerator
     */
    @Bean
    public CrewMemberGenerator crewMemberGenerator(Random random) {
        return new CrewMemberGenerator(random);
    }

    /**
     * Создаёт бин для SpaseShipGenerator.
     * Использует другие бины генераторов в качестве зависимостей.
     *
     * @param random генератор случайных чисел
     * @param dimensionsGenerator генератор размеров
     * @param engineGenerator генератор двигателей
     * @param crewMemberGenerator генератор членов экипажа
     * @return экземпляр SpaseShipGenerator
     */
    @Bean
    public SpaseShipGenerator spaseShipGenerator(
            Random random,
            DimensionsGenerator dimensionsGenerator,
            EngineGenerator engineGenerator,
            CrewMemberGenerator crewMemberGenerator) {
        return new SpaseShipGenerator(
                random,
                dimensionsGenerator,
                engineGenerator,
                crewMemberGenerator
        );
    }

    /**
     * Создаёт бин для RepairmanGenerator.
     *
     * @param random генератор случайных чисел
     * @return экземпляр RepairmanGenerator
     */
    @Bean
    public RepairmanGenerator repairmanGenerator(Random random) {
        return new RepairmanGenerator(random);
    }
}

