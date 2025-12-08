package ru.itmo.spaceships;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.itmo.spaceships.config.R2dbcConfig;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("functionalTest")
@Import(R2dbcConfig.class)
public abstract class BaseDbTest {
}
