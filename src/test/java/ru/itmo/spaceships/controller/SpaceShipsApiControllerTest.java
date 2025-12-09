package ru.itmo.spaceships.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.itmo.spaceships.BaseDbTest;
import ru.itmo.spaceships.generated.model.CrewMemberDto;
import ru.itmo.spaceships.generated.model.DimensionsDto;
import ru.itmo.spaceships.generated.model.EngineDto;
import ru.itmo.spaceships.generated.model.FuelType;
import ru.itmo.spaceships.generated.model.SpaceShipDto;
import ru.itmo.spaceships.generated.model.SpaceShipRequest;
import ru.itmo.spaceships.generated.model.SpaceShipType;
import ru.itmo.spaceships.repository.SpaceShipRepository;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpaceShipsApiControllerTest extends BaseDbTest {

    @Autowired
    WebTestClient webClient;

    @Autowired
    SpaceShipRepository spaceShipRepository;

    @Autowired
    ObjectMapper objectMapper;

    private static long serialCounter = System.currentTimeMillis();

    private SpaceShipRequest createTestRequest() {
        // Use a unique serial to avoid conflicts
        return createTestRequest(++serialCounter);
    }

    private SpaceShipRequest createTestRequest(Long serial) {
        SpaceShipRequest request = new SpaceShipRequest();
        request.setSerial(serial);
        request.setManufacturer("Test Manufacturer");
        request.setManufactureDate(OffsetDateTime.now());
        request.setName("Test Ship");
        request.setType(SpaceShipType.CARGO);

        DimensionsDto dimensions = new DimensionsDto();
        dimensions.setLength(100L);
        dimensions.setWidth(50L);
        dimensions.setHeight(30L);
        dimensions.setWeight(1000.0);
        dimensions.setVolume(150000.0);
        request.setDimensions(dimensions);

        EngineDto engine = new EngineDto();
        engine.setModel("Test Engine");
        engine.setThrust(5000);
        engine.setFuelType(FuelType.KEROSENE);
        engine.setFuelConsumption(100.0);
        request.setEngine(engine);

        List<CrewMemberDto> crew = new ArrayList<>();
        CrewMemberDto member = new CrewMemberDto();
        member.setFullName("John Doe");
        member.setRank("Captain");
        member.setExperienceYears(10);
        member.setBirthDate(LocalDate.of(1990, 1, 1));
        crew.add(member);
        request.setCrew(crew);

        request.setMaxSpeed(500);
        return request;
    }

    @Test
    void testCreateSpaceship() {
        SpaceShipRequest request = createTestRequest();

        webClient.post()
                .uri("/spaceships")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SpaceShipDto.class)
                .consumeWith(result -> {
                    SpaceShipDto dto = result.getResponseBody();
                    assertNotNull(dto);
                    assertNotNull(dto.getSerial());
                    assertEquals("Test Manufacturer", dto.getManufacturer());
                    assertEquals("Test Ship", dto.getName());
                    assertEquals(SpaceShipType.CARGO, dto.getType());
                    assertNotNull(dto.getDimensions());
                    assertEquals(100L, dto.getDimensions().getLength());
                    assertNotNull(dto.getEngine());
                    assertEquals("Test Engine", dto.getEngine().getModel());
                    assertNotNull(dto.getCrew());
                    assertEquals(1, dto.getCrew().size());
                    assertEquals(500, dto.getMaxSpeed());
                });
    }

    @Test
    void testCreateSpaceshipWithSerial() {
        long testSerial = 100000L + System.nanoTime() % 100000L; // Unique serial for this test
        SpaceShipRequest request = createTestRequest(testSerial);

        webClient.post()
                .uri("/spaceships")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SpaceShipDto.class)
                .consumeWith(result -> {
                    SpaceShipDto dto = result.getResponseBody();
                    assertNotNull(dto);
                    assertEquals(testSerial, dto.getSerial());
                    assertEquals("Test Manufacturer", dto.getManufacturer());
                });
    }

    @Test
    void testCreateSpaceshipWithoutSerial() {
        SpaceShipRequest request = createTestRequest(1L);
        request.setSerial(null);

        webClient.post()
                .uri("/spaceships")
                .bodyValue(request)
                .exchange()
                .expectStatus().is4xxClientError(); // 400 Bad Request - validation error
    }

    @Test
    void testUpdateSpaceship() {
        // Create a spaceship first
        SpaceShipRequest createRequest = createTestRequest();
        SpaceShipDto created = webClient.post()
                .uri("/spaceships")
                .bodyValue(createRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SpaceShipDto.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(created);
        Long serial = created.getSerial();

        // Update the spaceship
        SpaceShipRequest updateRequest = createTestRequest();
        updateRequest.setName("Updated Ship Name");
        updateRequest.setMaxSpeed(600);

        webClient.put()
                .uri("/spaceships/{serial}", serial)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SpaceShipDto.class)
                .consumeWith(result -> {
                    SpaceShipDto dto = result.getResponseBody();
                    assertNotNull(dto);
                    assertEquals(serial, dto.getSerial());
                    assertEquals("Updated Ship Name", dto.getName());
                    assertEquals(600, dto.getMaxSpeed());
                });
    }

    @Test
    void testUpdateSpaceshipIgnoresSerialInRequest() {
        // Create a spaceship first
        long originalSerial = 200000L + System.nanoTime() % 100000L; // Unique serial
        SpaceShipRequest createRequest = createTestRequest(originalSerial);
        SpaceShipDto created = webClient.post()
                .uri("/spaceships")
                .bodyValue(createRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SpaceShipDto.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(created);
        assertEquals(originalSerial, created.getSerial());

        // Update the spaceship with a different serial in request (should be ignored)
        long differentSerial = 300000L + System.nanoTime() % 100000L; // Different serial - should be ignored
        SpaceShipRequest updateRequest = createTestRequest(differentSerial);
        updateRequest.setName("Updated Ship Name");

        webClient.put()
                .uri("/spaceships/{serial}", originalSerial)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SpaceShipDto.class)
                .consumeWith(result -> {
                    SpaceShipDto dto = result.getResponseBody();
                    assertNotNull(dto);
                    // Serial should be from path parameter, not from request
                    assertEquals(originalSerial, dto.getSerial());
                    assertEquals("Updated Ship Name", dto.getName());
                });
    }

    @Test
    void testDeleteSpaceship() {
        // Create a spaceship first
        SpaceShipRequest request = createTestRequest();
        SpaceShipDto created = webClient.post()
                .uri("/spaceships")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SpaceShipDto.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(created);
        Long serial = created.getSerial();

        // Delete the spaceship
        webClient.delete()
                .uri("/spaceships/{serial}", serial)
                .exchange()
                .expectStatus().isOk();

        // Verify it's deleted
        webClient.get()
                .uri("/spaceships/{serial}", serial)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void testGetSpaceshipBySerial() {
        // Create a spaceship
        SpaceShipRequest request = createTestRequest();
        SpaceShipDto created = webClient.post()
                .uri("/spaceships")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SpaceShipDto.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(created);
        Long serial = created.getSerial();

        // Get the spaceship by serial
        webClient.get()
                .uri("/spaceships/{serial}", serial)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SpaceShipDto.class)
                .consumeWith(result -> {
                    SpaceShipDto dto = result.getResponseBody();
                    assertNotNull(dto);
                    assertEquals(serial, dto.getSerial());
                    assertEquals("Test Ship", dto.getName());
                });
    }

    @Test
    void testGetSpaceshipBySerialNotFound() {
        webClient.get()
                .uri("/spaceships/{serial}", 999L)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void testGetSpaceships() {
        // Get initial count (with large page size to get all)
        int initialCount = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/spaceships")
                        .queryParam("page", 0)
                        .queryParam("size", 100)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(SpaceShipDto.class)
                .returnResult()
                .getResponseBody()
                .size();

        // Create multiple spaceships
        long baseSerial = 400000L + System.nanoTime() % 100000L;
        for (int i = 0; i < 3; i++) {
            SpaceShipRequest request = createTestRequest(baseSerial + i);
            request.setName("Ship " + i);
            webClient.post()
                    .uri("/spaceships")
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isOk();
        }

        // Get all spaceships (with large page size to get all)
        webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/spaceships")
                        .queryParam("page", 0)
                        .queryParam("size", 100)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(SpaceShipDto.class)
                .consumeWith(result -> {
                    assertNotNull(result.getResponseBody());
                    assertEquals(initialCount + 3, result.getResponseBody().size());
                });
    }

    @Test
    void testGetSpaceshipsWithPaging() {
        // Create multiple spaceships
        long baseSerial = 500000L + System.nanoTime() % 100000L;
        for (int i = 0; i < 5; i++) {
            SpaceShipRequest request = createTestRequest(baseSerial + i);
            request.setName("Ship " + i);
            webClient.post()
                    .uri("/spaceships")
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isOk();
        }

        // Get first page
        webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/spaceships")
                        .queryParam("page", 0)
                        .queryParam("size", 2)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(SpaceShipDto.class)
                .consumeWith(result -> {
                    assertNotNull(result.getResponseBody());
                    assertEquals(2, result.getResponseBody().size());
                });

        // Get second page
        webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/spaceships")
                        .queryParam("page", 1)
                        .queryParam("size", 2)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(SpaceShipDto.class)
                .consumeWith(result -> {
                    assertNotNull(result.getResponseBody());
                    assertTrue(result.getResponseBody().size() <= 2);
                });
    }
}

