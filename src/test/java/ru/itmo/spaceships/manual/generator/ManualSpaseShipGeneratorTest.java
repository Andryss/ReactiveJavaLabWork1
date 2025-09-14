package ru.itmo.spaceships.manual.generator;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import ru.itmo.spaceships.generator.SpaseShipGenerator;
import ru.itmo.spaceships.model.SpaceShip;

@Slf4j
@Disabled("Только для ручного тестирования")
class ManualSpaseShipGeneratorTest {

    @Test
    void generateManyTest() {
        SpaseShipGenerator generator = new SpaseShipGenerator();

        List<SpaceShip> generated = generator.generateMany(1);

        log.info("Generated: {}", generated);
    }
}