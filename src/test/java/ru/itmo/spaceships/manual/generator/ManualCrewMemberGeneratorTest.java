package ru.itmo.spaceships.manual.generator;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import ru.itmo.spaceships.generator.CrewMemberGenerator;
import ru.itmo.spaceships.model.CrewMember;

@Slf4j
@Disabled("Только для ручного тестирования")
class ManualCrewMemberGeneratorTest {

    @Test
    void generateManyTest() {
        CrewMemberGenerator generator = new CrewMemberGenerator();

        List<CrewMember> generated = generator.generateMany(10);

        log.info("Generated: {}", generated);
    }
}