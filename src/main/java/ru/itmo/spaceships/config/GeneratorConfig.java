package ru.itmo.spaceships.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.itmo.spaceships.generator.CrewMemberGenerator;
import ru.itmo.spaceships.generator.DimensionsGenerator;
import ru.itmo.spaceships.generator.EngineGenerator;
import ru.itmo.spaceships.generator.SpaseShipGenerator;

/**
 * Configuration for generator beans.
 */
@Configuration
public class GeneratorConfig {

    /**
     * Creates a bean for DimensionsGenerator.
     *
     * @return DimensionsGenerator instance
     */
    @Bean
    public DimensionsGenerator dimensionsGenerator() {
        return new DimensionsGenerator();
    }

    /**
     * Creates a bean for EngineGenerator.
     *
     * @return EngineGenerator instance
     */
    @Bean
    public EngineGenerator engineGenerator() {
        return new EngineGenerator();
    }

    /**
     * Creates a bean for CrewMemberGenerator.
     *
     * @return CrewMemberGenerator instance
     */
    @Bean
    public CrewMemberGenerator crewMemberGenerator() {
        return new CrewMemberGenerator();
    }

    /**
     * Creates a bean for SpaseShipGenerator.
     * Uses the other generator beans for dependencies.
     *
     * @param dimensionsGenerator dimensions generator
     * @param engineGenerator engine generator
     * @param crewMemberGenerator crew member generator
     * @return SpaseShipGenerator instance
     */
    @Bean
    public SpaseShipGenerator spaseShipGenerator(
            DimensionsGenerator dimensionsGenerator,
            EngineGenerator engineGenerator,
            CrewMemberGenerator crewMemberGenerator) {
        return new SpaseShipGenerator(
                new java.util.Random(),
                dimensionsGenerator,
                engineGenerator,
                crewMemberGenerator
        );
    }
}

