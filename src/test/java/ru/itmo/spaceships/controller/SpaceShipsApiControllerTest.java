package ru.itmo.spaceships.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;
import ru.itmo.spaceships.BaseDbTest;
import ru.itmo.spaceships.generated.model.CrewMemberDto;
import ru.itmo.spaceships.generated.model.DimensionsDto;
import ru.itmo.spaceships.generated.model.EngineDto;
import ru.itmo.spaceships.generated.model.FuelType;
import ru.itmo.spaceships.generated.model.ErrorObject;
import ru.itmo.spaceships.generated.model.SpaceShipDto;
import ru.itmo.spaceships.generated.model.SpaceShipRequest;
import ru.itmo.spaceships.generated.model.SpaceShipType;
import ru.itmo.spaceships.repository.SpaceShipRepository;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

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
        // Используем уникальный серийный номер, чтобы избежать конфликтов
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
                .expectStatus().isBadRequest() // Теперь правильно обрабатывается как 400
                .expectBody(ErrorObject.class)
                .consumeWith(result -> {
                    ErrorObject error = result.getResponseBody();
                    assertNotNull(error);
                    assertEquals(400, error.getCode());
                    // Может быть либо spaceship.serial.required.error (наша ошибка), либо validation.error (Spring)
                    assertTrue("spaceship.serial.required.error".equals(error.getMessage()) ||
                               "validation.error".equals(error.getMessage()));
                    assertNotNull(error.getHumanMessage());
                });
    }

    @Test
    void testUpdateSpaceship() {
        // Сначала создаём корабль
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

        // Обновляем корабль
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
    void testUpdateSpaceshipNotFound() {
        SpaceShipRequest updateRequest = createTestRequest();
        updateRequest.setName("Updated Ship Name");

        webClient.put()
                .uri("/spaceships/{serial}", 999L)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorObject.class)
                .consumeWith(result -> {
                    ErrorObject error = result.getResponseBody();
                    assertNotNull(error);
                    assertEquals(404, error.getCode());
                    assertEquals("spaceship.absent.error", error.getMessage());
                    assertNotNull(error.getHumanMessage());
                    assertTrue(error.getHumanMessage().contains("Корабль с серийным номером=\"999\" не найден"));
                });
    }

    @Test
    void testDeleteSpaceshipNotFound() {
        webClient.delete()
                .uri("/spaceships/{serial}", 999L)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorObject.class)
                .consumeWith(result -> {
                    ErrorObject error = result.getResponseBody();
                    assertNotNull(error);
                    assertEquals(404, error.getCode());
                    assertEquals("spaceship.absent.error", error.getMessage());
                    assertNotNull(error.getHumanMessage());
                    assertTrue(error.getHumanMessage().contains("Корабль с серийным номером=\"999\" не найден"));
                });
    }

    @Test
    void testUpdateSpaceshipIgnoresSerialInRequest() {
        // Сначала создаём корабль
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

        // Обновляем корабль с другим серийным номером в запросе (должен быть проигнорирован)
        long differentSerial = 300000L + System.nanoTime() % 100000L; // Другой серийный номер - должен быть проигнорирован
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
                    // Серийный номер должен быть из параметра пути, а не из запроса
                    assertEquals(originalSerial, dto.getSerial());
                    assertEquals("Updated Ship Name", dto.getName());
                });
    }

    @Test
    void testDeleteSpaceship() {
        // Сначала создаём корабль
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

        // Удаляем корабль
        webClient.delete()
                .uri("/spaceships/{serial}", serial)
                .exchange()
                .expectStatus().isOk();

        // Проверяем, что он удалён
        webClient.get()
                .uri("/spaceships/{serial}", serial)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorObject.class)
                .consumeWith(result -> {
                    ErrorObject error = result.getResponseBody();
                    assertNotNull(error);
                    assertEquals(404, error.getCode());
                    assertEquals("spaceship.absent.error", error.getMessage());
                    assertNotNull(error.getHumanMessage());
                });
    }

    @Test
    void testGetSpaceshipBySerial() {
        // Создаём корабль
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

        // Получаем корабль по серийному номеру
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
                .expectStatus().isNotFound()
                .expectBody(ErrorObject.class)
                .consumeWith(result -> {
                    ErrorObject error = result.getResponseBody();
                    assertNotNull(error);
                    assertEquals(404, error.getCode());
                    assertEquals("spaceship.absent.error", error.getMessage());
                    assertNotNull(error.getHumanMessage());
                    assertTrue(error.getHumanMessage().contains("Корабль с серийным номером=\"999\" не найден"));
                });
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

        // Получаем все корабли (с большим размером страницы, чтобы получить все)
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

        // Получаем первую страницу
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

        // Получаем вторую страницу
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

    @Test
    void testGetSpaceshipsUpdatesStreamMultipleUpdates() {
        CountDownLatch consumerSubscribedLatch = new CountDownLatch(1);

        // Создаём первый корабль
        SpaceShipRequest createRequest1 = createTestRequest();
        SpaceShipDto created1 = webClient.post()
                .uri("/spaceships")
                .bodyValue(createRequest1)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SpaceShipDto.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(created1);
        Long serial1 = created1.getSerial();

        // Создаём второй корабль
        SpaceShipRequest createRequest2 = createTestRequest();
        SpaceShipDto created2 = webClient.post()
                .uri("/spaceships")
                .bodyValue(createRequest2)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SpaceShipDto.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(created2);
        Long serial2 = created2.getSerial();

        // Публикуем обновления обоих кораблей
        new Thread(() -> {
            SpaceShipRequest updateRequest1 = createTestRequest(serial1);
            updateRequest1.setName("Updated Ship Name 1");
            updateRequest1.setManufacturer("Updated Manufacturer 1");

            WebTestClient.RequestHeadersSpec<?> requestSpec = webClient.put()
                    .uri("/spaceships/{serial}", serial1)
                    .bodyValue(updateRequest1);

            try {
                consumerSubscribedLatch.await();
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            requestSpec.exchange()
                    .expectStatus().isOk();
        }).start();

        new Thread(() -> {
            SpaceShipRequest updateRequest2 = createTestRequest(serial2);
            updateRequest2.setName("Updated Ship Name 2");
            updateRequest2.setManufacturer("Updated Manufacturer 2");

            WebTestClient.RequestHeadersSpec<?> requestSpec = webClient.put()
                    .uri("/spaceships/{serial}", serial2)
                    .bodyValue(updateRequest2);

            try {
                consumerSubscribedLatch.await();
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            requestSpec.exchange()
                    .expectStatus().isOk();
        }).start();

        consumerSubscribedLatch.countDown();

        // Подключаемся к стриму обновлений и фильтруем по серийным номерам наших кораблей
        webClient.get()
                .uri("/spaceships/updates/stream")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM)
                .returnResult(SpaceShipDto.class)
                .getResponseBody()
                .filter(dto -> dto.getSerial().equals(serial1) || dto.getSerial().equals(serial2))
                .take(2)
                .collectList()
                .as(StepVerifier::create)
                .assertNext(updates -> {
                    assertEquals(2, updates.size(), "Should receive exactly 2 updates");

                    // Проверяем, что получили оба обновления (одно для serial1, одно для serial2)
                    boolean found1 = false;
                    boolean found2 = false;

                    for (SpaceShipDto dto : updates) {
                        if (dto.getSerial().equals(serial1)) {
                            assertEquals("Updated Ship Name 1", dto.getName());
                            assertEquals("Updated Manufacturer 1", dto.getManufacturer());
                            found1 = true;
                        } else if (dto.getSerial().equals(serial2)) {
                            assertEquals("Updated Ship Name 2", dto.getName());
                            assertEquals("Updated Manufacturer 2", dto.getManufacturer());
                            found2 = true;
                        }
                    }

                    assertTrue(found1, "Should receive update for first spaceship (serial=" + serial1 + ")");
                    assertTrue(found2, "Should receive update for second spaceship (serial=" + serial2 + ")");
                })
                .verifyComplete();
    }
}

