package ru.itmo.spaceships.manual.generator;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import ru.itmo.spaceships.generator.DimensionsGenerator;
import ru.itmo.spaceships.model.Dimensions;

@Slf4j
@Disabled("Только для ручного тестирования")
class ManualDimensionsGeneratorTest {

    @Test
    void generateManyTest() {
        DimensionsGenerator generator = new DimensionsGenerator();

        List<Dimensions> generated = generator.generateMany(10);

        log.info("Generated: {}", generated);
    }
}