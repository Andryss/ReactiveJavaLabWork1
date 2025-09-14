package ru.itmo.spaceships.manual.generator;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import ru.itmo.spaceships.generator.EngineGenerator;
import ru.itmo.spaceships.model.Engine;

@Slf4j
@Disabled("Только для ручного тестирования")
class ManualEngineGeneratorTest {

    @Test
    void generateManyTest() {
        EngineGenerator generator = new EngineGenerator();

        List<Engine> generated = generator.generateMany(10);

        log.info("Generated: {}", generated);
    }
}