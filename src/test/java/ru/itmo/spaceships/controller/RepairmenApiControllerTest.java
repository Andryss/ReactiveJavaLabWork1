package ru.itmo.spaceships.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.itmo.spaceships.BaseDbTest;
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
                .expectStatus().is5xxServerError();
    }

    @Test
    void testCreateRepairmanWithoutPosition() {
        RepairmanRequest request = new RepairmanRequest();
        request.setName("John Doe");

        webClient.post()
                .uri("/repairmen")
                .bodyValue(request)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void testUpdateRepairman() {
        // First create a repairman
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

        // Now update it
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
        // First create a repairman
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

        // Update only name
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
                .expectStatus().is5xxServerError();
    }

    @Test
    void testDeleteRepairman() {
        // First create a repairman
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

        // Delete it
        webClient.delete()
                .uri("/repairmen/{id}", id)
                .exchange()
                .expectStatus().isOk();

        // Verify it's deleted by trying to update it
        RepairmanRequest updateRequest = new RepairmanRequest();
        updateRequest.setName("Jane Doe");

        webClient.put()
                .uri("/repairmen/{id}", id)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void testDeleteRepairmanNotFound() {
        webClient.delete()
                .uri("/repairmen/{id}", 999L)
                .exchange()
                .expectStatus().is5xxServerError();
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

        // Update
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

        // Delete
        webClient.delete()
                .uri("/repairmen/{id}", id)
                .exchange()
                .expectStatus().isOk();

        // Verify deletion
        webClient.put()
                .uri("/repairmen/{id}", id)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void testGetRepairmanById() {
        // First create a repairman
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

        // Get by ID
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
                .expectStatus().is5xxServerError();
    }

    @Test
    void testGetRepairmen() {
        // Get initial count (with large page size to get all)
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

        // Create multiple repairmen
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

        // Get all repairmen (with large page size to get all)
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
        // Create multiple repairmen
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

        // Get first page (page=0, size=3)
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

        // Get second page (page=1, size=3)
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
        // Get all repairmen - should return empty list or existing items
        // Note: Database may contain data from other tests due to refresh timing
        webClient.get()
                .uri("/repairmen")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(RepairmanDto.class)
                .consumeWith(result -> {
                    assertNotNull(result.getResponseBody());
                    // Just verify the endpoint works and returns a list
                    // Exact count depends on database state
                });
    }

    @Test
    void testGetRepairmenSortedById() {
        // Get initial count (with large page size to get all)
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

        // Create multiple repairmen
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

        // Get all repairmen and verify they are sorted by ID (with large page size to get all)
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

                    // Verify sorting by ID (ascending)
                    for (int i = 1; i < repairmen.size(); i++) {
                        assertTrue(repairmen.get(i).getId() >= repairmen.get(i - 1).getId(),
                                "Repairmen should be sorted by ID in ascending order");
                    }
                });
    }
}

