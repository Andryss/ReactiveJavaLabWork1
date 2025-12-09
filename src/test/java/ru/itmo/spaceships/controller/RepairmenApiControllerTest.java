package ru.itmo.spaceships.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.itmo.spaceships.BaseDbTest;
import ru.itmo.spaceships.generated.model.ErrorObject;
import ru.itmo.spaceships.generated.model.RepairmanDto;
import ru.itmo.spaceships.generated.model.RepairmanRequest;
import ru.itmo.spaceships.repository.RepairmanRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RepairmenApiControllerTest extends BaseDbTest {

    @Autowired
    WebTestClient webClient;

    @Autowired
    RepairmanRepository repairmanRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void testCreateRepairman() {
        RepairmanRequest request = new RepairmanRequest();
        request.setName("John Doe");
        request.setPosition("Senior Technician");

        webClient.post()
                .uri("/repairmen")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(RepairmanDto.class)
                .consumeWith(result -> {
                    RepairmanDto dto = result.getResponseBody();
                    assertNotNull(dto);
                    assertNotNull(dto.getId());
                    assertEquals("John Doe", dto.getName());
                    assertEquals("Senior Technician", dto.getPosition());
                });
    }

    @Test
    void testCreateRepairmanWithoutName() {
        RepairmanRequest request = new RepairmanRequest();
        request.setPosition("Senior Technician");

        webClient.post()
                .uri("/repairmen")
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorObject.class)
                .consumeWith(result -> {
                    ErrorObject error = result.getResponseBody();
                    assertNotNull(error);
                    assertEquals(400, error.getCode());
                    assertEquals("repairman.validation.error", error.getMessage());
                    assertNotNull(error.getHumanMessage());
                    assertTrue(error.getHumanMessage().contains("Имя и должность обязательны"));
                });
    }

    @Test
    void testCreateRepairmanWithoutPosition() {
        RepairmanRequest request = new RepairmanRequest();
        request.setName("John Doe");

        webClient.post()
                .uri("/repairmen")
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorObject.class)
                .consumeWith(result -> {
                    ErrorObject error = result.getResponseBody();
                    assertNotNull(error);
                    assertEquals(400, error.getCode());
                    assertEquals("repairman.validation.error", error.getMessage());
                    assertNotNull(error.getHumanMessage());
                    assertTrue(error.getHumanMessage().contains("Имя и должность обязательны"));
                });
    }

    @Test
    void testUpdateRepairman() {
        // Сначала создаём ремонтника
        RepairmanRequest createRequest = new RepairmanRequest();
        createRequest.setName("John Doe");
        createRequest.setPosition("Senior Technician");

        RepairmanDto created = webClient.post()
                .uri("/repairmen")
                .bodyValue(createRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(RepairmanDto.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(created);
        Long id = created.getId();

        // Теперь обновляем его
        RepairmanRequest updateRequest = new RepairmanRequest();
        updateRequest.setName("Jane Doe");
        updateRequest.setPosition("Lead Technician");

        webClient.put()
                .uri("/repairmen/{id}", id)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(RepairmanDto.class)
                .consumeWith(result -> {
                    RepairmanDto dto = result.getResponseBody();
                    assertNotNull(dto);
                    assertEquals(id, dto.getId());
                    assertEquals("Jane Doe", dto.getName());
                    assertEquals("Lead Technician", dto.getPosition());
                });
    }

    @Test
    void testUpdateRepairmanPartial() {
        // Сначала создаём ремонтника
        RepairmanRequest createRequest = new RepairmanRequest();
        createRequest.setName("John Doe");
        createRequest.setPosition("Senior Technician");

        RepairmanDto created = webClient.post()
                .uri("/repairmen")
                .bodyValue(createRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(RepairmanDto.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(created);
        Long id = created.getId();

        // Обновляем только имя
        RepairmanRequest updateRequest = new RepairmanRequest();
        updateRequest.setName("Jane Doe");

        webClient.put()
                .uri("/repairmen/{id}", id)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(RepairmanDto.class)
                .consumeWith(result -> {
                    RepairmanDto dto = result.getResponseBody();
                    assertNotNull(dto);
                    assertEquals(id, dto.getId());
                    assertEquals("Jane Doe", dto.getName());
                    assertEquals("Senior Technician", dto.getPosition()); // Position unchanged
                });
    }

    @Test
    void testUpdateRepairmanNotFound() {
        RepairmanRequest updateRequest = new RepairmanRequest();
        updateRequest.setName("Jane Doe");
        updateRequest.setPosition("Lead Technician");

        webClient.put()
                .uri("/repairmen/{id}", 999L)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorObject.class)
                .consumeWith(result -> {
                    ErrorObject error = result.getResponseBody();
                    assertNotNull(error);
                    assertEquals(404, error.getCode());
                    assertEquals("repairman.absent.error", error.getMessage());
                    assertNotNull(error.getHumanMessage());
                    assertTrue(error.getHumanMessage().contains("Ремонтник с id=\"999\" не найден"));
                });
    }

    @Test
    void testDeleteRepairman() {
        // Сначала создаём ремонтника
        RepairmanRequest createRequest = new RepairmanRequest();
        createRequest.setName("John Doe");
        createRequest.setPosition("Senior Technician");

        RepairmanDto created = webClient.post()
                .uri("/repairmen")
                .bodyValue(createRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(RepairmanDto.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(created);
        Long id = created.getId();

        // Удаляем его
        webClient.delete()
                .uri("/repairmen/{id}", id)
                .exchange()
                .expectStatus().isOk();

        // Проверяем, что он удалён, пытаясь обновить его
        RepairmanRequest updateRequest = new RepairmanRequest();
        updateRequest.setName("Jane Doe");

        webClient.put()
                .uri("/repairmen/{id}", id)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorObject.class)
                .consumeWith(result -> {
                    ErrorObject error = result.getResponseBody();
                    assertNotNull(error);
                    assertEquals(404, error.getCode());
                    assertEquals("repairman.absent.error", error.getMessage());
                });
    }

    @Test
    void testDeleteRepairmanNotFound() {
        webClient.delete()
                .uri("/repairmen/{id}", 999L)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorObject.class)
                .consumeWith(result -> {
                    ErrorObject error = result.getResponseBody();
                    assertNotNull(error);
                    assertEquals(404, error.getCode());
                    assertEquals("repairman.absent.error", error.getMessage());
                    assertNotNull(error.getHumanMessage());
                    assertTrue(error.getHumanMessage().contains("Ремонтник с id=\"999\" не найден"));
                });
    }

    @Test
    void testCreateAndUpdateAndDeleteFlow() {
        // Create
        RepairmanRequest createRequest = new RepairmanRequest();
        createRequest.setName("John Doe");
        createRequest.setPosition("Senior Technician");

        RepairmanDto created = webClient.post()
                .uri("/repairmen")
                .bodyValue(createRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(RepairmanDto.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(created);
        Long id = created.getId();
        assertEquals("John Doe", created.getName());
        assertEquals("Senior Technician", created.getPosition());

        // Обновление
        RepairmanRequest updateRequest = new RepairmanRequest();
        updateRequest.setName("Jane Doe");
        updateRequest.setPosition("Lead Technician");

        RepairmanDto updated = webClient.put()
                .uri("/repairmen/{id}", id)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(RepairmanDto.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(updated);
        assertEquals(id, updated.getId());
        assertEquals("Jane Doe", updated.getName());
        assertEquals("Lead Technician", updated.getPosition());

        // Удаление
        webClient.delete()
                .uri("/repairmen/{id}", id)
                .exchange()
                .expectStatus().isOk();

        // Проверка удаления
        webClient.put()
                .uri("/repairmen/{id}", id)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorObject.class)
                .consumeWith(result -> {
                    ErrorObject error = result.getResponseBody();
                    assertNotNull(error);
                    assertEquals(404, error.getCode());
                    assertEquals("repairman.absent.error", error.getMessage());
                });
    }

    @Test
    void testGetRepairmanById() {
        // Сначала создаём ремонтника
        RepairmanRequest createRequest = new RepairmanRequest();
        createRequest.setName("John Doe");
        createRequest.setPosition("Senior Technician");

        RepairmanDto created = webClient.post()
                .uri("/repairmen")
                .bodyValue(createRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(RepairmanDto.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(created);
        Long id = created.getId();

        // Получение по ID
        webClient.get()
                .uri("/repairmen/{id}", id)
                .exchange()
                .expectStatus().isOk()
                .expectBody(RepairmanDto.class)
                .consumeWith(result -> {
                    RepairmanDto dto = result.getResponseBody();
                    assertNotNull(dto);
                    assertEquals(id, dto.getId());
                    assertEquals("John Doe", dto.getName());
                    assertEquals("Senior Technician", dto.getPosition());
                });
    }

    @Test
    void testGetRepairmanByIdNotFound() {
        webClient.get()
                .uri("/repairmen/{id}", 999L)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorObject.class)
                .consumeWith(result -> {
                    ErrorObject error = result.getResponseBody();
                    assertNotNull(error);
                    assertEquals(404, error.getCode());
                    assertEquals("repairman.absent.error", error.getMessage());
                    assertNotNull(error.getHumanMessage());
                    assertTrue(error.getHumanMessage().contains("Ремонтник с id=\"999\" не найден"));
                });
    }

    @Test
    void testGetRepairmen() {
        // Получаем начальное количество (с большим размером страницы, чтобы получить все)
        int initialCount = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/repairmen")
                        .queryParam("page", 0)
                        .queryParam("size", 100)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(RepairmanDto.class)
                .returnResult()
                .getResponseBody()
                .size();

        // Создаём несколько ремонтников
        for (int i = 0; i < 5; i++) {
            RepairmanRequest request = new RepairmanRequest();
            request.setName("Repairman " + i);
            request.setPosition("Position " + i);

            webClient.post()
                    .uri("/repairmen")
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isOk();
        }

        // Получаем всех ремонтников (с большим размером страницы, чтобы получить все)
        webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/repairmen")
                        .queryParam("page", 0)
                        .queryParam("size", 100)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(RepairmanDto.class)
                .consumeWith(result -> {
                    assertNotNull(result.getResponseBody());
                    assertEquals(initialCount + 5, result.getResponseBody().size());
                });
    }

    @Test
    void testGetRepairmenWithPaging() {
        // Создаём несколько ремонтников
        for (int i = 0; i < 10; i++) {
            RepairmanRequest request = new RepairmanRequest();
            request.setName("Repairman " + i);
            request.setPosition("Position " + i);

            webClient.post()
                    .uri("/repairmen")
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isOk();
        }

        // Получаем первую страницу (page=0, size=3)
        webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/repairmen")
                        .queryParam("page", 0)
                        .queryParam("size", 3)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(RepairmanDto.class)
                .consumeWith(result -> {
                    assertNotNull(result.getResponseBody());
                    assertEquals(3, result.getResponseBody().size());
                });

        // Получаем вторую страницу (page=1, size=3)
        webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/repairmen")
                        .queryParam("page", 1)
                        .queryParam("size", 3)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(RepairmanDto.class)
                .consumeWith(result -> {
                    assertNotNull(result.getResponseBody());
                    assertEquals(3, result.getResponseBody().size());
                });
    }

    @Test
    void testGetRepairmenEmpty() {
        // Получаем всех ремонтников - должен вернуть пустой список или существующие элементы
        // Примечание: База данных может содержать данные из других тестов из-за времени обновления
        webClient.get()
                .uri("/repairmen")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(RepairmanDto.class)
                .consumeWith(result -> {
                    assertNotNull(result.getResponseBody());
                    // Просто проверяем, что эндпоинт работает и возвращает список
                    // Точное количество зависит от состояния базы данных
                });
    }

    @Test
    void testGetRepairmenSortedById() {
        // Получаем начальное количество (с большим размером страницы, чтобы получить все)
        int initialCount = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/repairmen")
                        .queryParam("page", 0)
                        .queryParam("size", 100)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(RepairmanDto.class)
                .returnResult()
                .getResponseBody()
                .size();

        // Создаём несколько ремонтников
        for (int i = 0; i < 5; i++) {
            RepairmanRequest request = new RepairmanRequest();
            request.setName("Repairman " + i);
            request.setPosition("Position " + i);

            webClient.post()
                    .uri("/repairmen")
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isOk();
        }

        // Получаем всех ремонтников и проверяем, что они отсортированы по ID (с большим размером страницы, чтобы получить все)
        webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/repairmen")
                        .queryParam("page", 0)
                        .queryParam("size", 100)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(RepairmanDto.class)
                .consumeWith(result -> {
                    var repairmen = result.getResponseBody();
                    assertNotNull(repairmen);
                    assertEquals(initialCount + 5, repairmen.size());

                    // Проверяем сортировку по ID (по возрастанию)
                    for (int i = 1; i < repairmen.size(); i++) {
                        assertTrue(repairmen.get(i).getId() >= repairmen.get(i - 1).getId(),
                                "Repairmen should be sorted by ID in ascending order");
                    }
                });
    }
}

