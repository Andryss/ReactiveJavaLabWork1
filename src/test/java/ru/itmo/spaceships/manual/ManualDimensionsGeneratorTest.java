package ru.itmo.spaceships.manual;

import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itmo.spaceships.generator.DimensionsGenerator;
import ru.itmo.spaceships.model.Dimensions;

@Disabled("Только для ручного тестирования")
class ManualDimensionsGeneratorTest {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    void generateManyTest() {
        DimensionsGenerator generator = new DimensionsGenerator();

        List<Dimensions> generated = generator.generateMany(10);

        logger.info("Generated: {}", generated);
    }
}