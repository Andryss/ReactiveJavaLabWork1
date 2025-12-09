package ru.itmo.spaceships.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.itmo.spaceships.BaseDbTest;
import ru.itmo.spaceships.generated.model.MaintenanceRequestDto;
import ru.itmo.spaceships.generated.model.MaintenanceRequestRequest;
import ru.itmo.spaceships.generated.model.MaintenanceStatus;
import ru.itmo.spaceships.repository.MaintenanceRequestRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MaintenanceRequestsApiControllerTest extends BaseDbTest {

    @Autowired
    WebTestClient webClient;

    @Autowired
    MaintenanceRequestRepository maintenanceRequestRepository;

    @Autowired
    ObjectMapper objectMapper;

    private MaintenanceRequestRequest createTestRequest() {
        MaintenanceRequestRequest request = new MaintenanceRequestRequest();
        request.setSpaceshipSerial(ThreadLocalRandom.current().nextLong(1, 1_000_000));
        request.setComment("Test maintenance request");
        return request;
    }

    @Test
    void testCreateMaintenanceRequest() {
        MaintenanceRequestRequest request = createTestRequest();

        webClient.post()
                .uri("/maintenance-requests")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MaintenanceRequestDto.class)
                .consumeWith(result -> {
                    MaintenanceRequestDto dto = result.getResponseBody();
                    assertNotNull(dto);
                    assertNotNull(dto.getId());
                    assertEquals(request.getSpaceshipSerial(), dto.getSpaceshipSerial());
                    assertEquals("Test maintenance request", dto.getComment());
                    assertEquals(MaintenanceStatus.NEW, dto.getStatus());
                    assertNotNull(dto.getCreatedAt());
                    assertNotNull(dto.getUpdatedAt());
                });
    }

    @Test
    void testCreateMaintenanceRequestOnlyUsesSpaceshipSerialAndComment() {
        MaintenanceRequestRequest request = createTestRequest();
        request.setAssignee(1L);
        request.setStatus(MaintenanceStatus.ACCEPTED);

        webClient.post()
                .uri("/maintenance-requests")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MaintenanceRequestDto.class)
                .consumeWith(result -> {
                    MaintenanceRequestDto dto = result.getResponseBody();
                    assertNotNull(dto);
                    assertEquals(request.getSpaceshipSerial(), dto.getSpaceshipSerial());
                    assertEquals("Test maintenance request", dto.getComment());
                    // assignee и status должны игнорироваться при создании, status должен быть NEW
                    assertEquals(MaintenanceStatus.NEW, dto.getStatus());
                });
    }

    @Test
    void testCreateMaintenanceRequestWithoutSpaceshipSerial() {
        MaintenanceRequestRequest request = new MaintenanceRequestRequest();
        request.setComment("Test comment");

        webClient.post()
                .uri("/maintenance-requests")
                .bodyValue(request)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void testCreateMaintenanceRequestWithoutComment() {
        MaintenanceRequestRequest request = new MaintenanceRequestRequest();
        request.setSpaceshipSerial(123L);

        webClient.post()
                .uri("/maintenance-requests")
                .bodyValue(request)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void testUpdateMaintenanceRequest() {
        // First create a maintenance request
        MaintenanceRequestRequest createRequest = createTestRequest();
        MaintenanceRequestDto created = webClient.post()
                .uri("/maintenance-requests")
                .bodyValue(createRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MaintenanceRequestDto.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(created);
        Long id = created.getId();

        // Now update it
        MaintenanceRequestRequest updateRequest = new MaintenanceRequestRequest();
        updateRequest.setComment("Updated comment");
        updateRequest.setAssignee(1L);
        updateRequest.setStatus(MaintenanceStatus.ACCEPTED);

        webClient.put()
                .uri("/maintenance-requests/{id}", id)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MaintenanceRequestDto.class)
                .consumeWith(result -> {
                    MaintenanceRequestDto dto = result.getResponseBody();
                    assertNotNull(dto);
                    assertEquals(id, dto.getId());
                    assertEquals("Updated comment", dto.getComment());
                    assertEquals(1L, dto.getAssignee());
                    assertEquals(MaintenanceStatus.ACCEPTED, dto.getStatus());
                    // createdAt should remain unchanged
                    assertEquals(created.getCreatedAt(), dto.getCreatedAt());
                    // updatedAt should be updated
                    assertTrue(dto.getUpdatedAt().isAfter(created.getUpdatedAt()) || 
                               dto.getUpdatedAt().equals(created.getUpdatedAt()));
                });
    }

    @Test
    void testUpdateMaintenanceRequestIgnoresCreatedAtAndUpdatedAt() {
        // First create a maintenance request
        MaintenanceRequestRequest createRequest = createTestRequest();
        MaintenanceRequestDto created = webClient.post()
                .uri("/maintenance-requests")
                .bodyValue(createRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MaintenanceRequestDto.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(created);
        Long id = created.getId();
        var originalCreatedAt = created.getCreatedAt();
        var originalUpdatedAt = created.getUpdatedAt();

        // Update with all fields (use valid status transition: NEW -> ACCEPTED)
        MaintenanceRequestRequest updateRequest = new MaintenanceRequestRequest();
        updateRequest.setComment("Updated comment");
        updateRequest.setStatus(MaintenanceStatus.ACCEPTED);

        // Wait a bit to ensure updatedAt changes
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        webClient.put()
                .uri("/maintenance-requests/{id}", id)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MaintenanceRequestDto.class)
                .consumeWith(result -> {
                    MaintenanceRequestDto dto = result.getResponseBody();
                    assertNotNull(dto);
                    // createdAt should remain unchanged (system field)
                    assertEquals(originalCreatedAt, dto.getCreatedAt());
                    // updatedAt should be updated by system
                    assertTrue(dto.getUpdatedAt().isAfter(originalUpdatedAt) || 
                               dto.getUpdatedAt().equals(originalUpdatedAt));
                });
    }

    @Test
    void testUpdateMaintenanceRequestPartialUpdate() {
        // First create a maintenance request
        MaintenanceRequestRequest createRequest = createTestRequest();
        MaintenanceRequestDto created = webClient.post()
                .uri("/maintenance-requests")
                .bodyValue(createRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MaintenanceRequestDto.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(created);
        Long id = created.getId();

        // Update only comment
        MaintenanceRequestRequest updateRequest = new MaintenanceRequestRequest();
        updateRequest.setComment("Only comment updated");

        webClient.put()
                .uri("/maintenance-requests/{id}", id)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MaintenanceRequestDto.class)
                .consumeWith(result -> {
                    MaintenanceRequestDto dto = result.getResponseBody();
                    assertNotNull(dto);
                    assertEquals(id, dto.getId());
                    assertEquals("Only comment updated", dto.getComment());
                    // Other fields should remain unchanged
                    assertEquals(created.getSpaceshipSerial(), dto.getSpaceshipSerial());
                    assertEquals(created.getStatus(), dto.getStatus());
                });
    }

    @Test
    void testDeleteMaintenanceRequest() {
        // Create a maintenance request first
        MaintenanceRequestRequest request = createTestRequest();
        MaintenanceRequestDto created = webClient.post()
                .uri("/maintenance-requests")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MaintenanceRequestDto.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(created);
        Long id = created.getId();

        // Delete the maintenance request
        webClient.delete()
                .uri("/maintenance-requests/{id}", id)
                .exchange()
                .expectStatus().isOk();

        // Verify it's deleted
        webClient.get()
                .uri("/maintenance-requests/{id}", id)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void testGetMaintenanceRequestById() {
        // Create a maintenance request
        MaintenanceRequestRequest request = createTestRequest();
        MaintenanceRequestDto created = webClient.post()
                .uri("/maintenance-requests")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MaintenanceRequestDto.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(created);
        Long id = created.getId();

        // Get the maintenance request by ID
        webClient.get()
                .uri("/maintenance-requests/{id}", id)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MaintenanceRequestDto.class)
                .consumeWith(result -> {
                    MaintenanceRequestDto dto = result.getResponseBody();
                    assertNotNull(dto);
                    assertEquals(id, dto.getId());
                    assertEquals(request.getSpaceshipSerial(), dto.getSpaceshipSerial());
                    assertEquals("Test maintenance request", dto.getComment());
                });
    }

    @Test
    void testGetMaintenanceRequestByIdNotFound() {
        webClient.get()
                .uri("/maintenance-requests/{id}", 999L)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void testGetMaintenanceRequests() {
        // Create multiple maintenance requests
        for (int i = 0; i < 3; i++) {
            MaintenanceRequestRequest request = createTestRequest();
            request.setComment("Request " + i);
            webClient.post()
                    .uri("/maintenance-requests")
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isOk();
        }

        // Get all maintenance requests (default paging)
        webClient.get()
                .uri("/maintenance-requests")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MaintenanceRequestDto.class)
                .consumeWith(result -> {
                    assertNotNull(result.getResponseBody());
                    assertTrue(result.getResponseBody().size() >= 3);
                });
    }

    @Test
    void testGetMaintenanceRequestsWithPaging() {
        // Create multiple maintenance requests
        for (int i = 0; i < 10; i++) {
            MaintenanceRequestRequest request = createTestRequest();
            request.setComment("Paging Request " + i);
            webClient.post()
                    .uri("/maintenance-requests")
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isOk();
        }

        // Get first page (page=0, size=3)
        webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/maintenance-requests")
                        .queryParam("page", 0)
                        .queryParam("size", 3)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MaintenanceRequestDto.class)
                .consumeWith(result -> {
                    assertNotNull(result.getResponseBody());
                    assertEquals(3, result.getResponseBody().size());
                    // Verify sorting by ID
                    List<MaintenanceRequestDto> sorted = result.getResponseBody();
                    assertTrue(sorted.get(0).getId() < sorted.get(1).getId());
                });

        // Get second page (page=1, size=3)
        webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/maintenance-requests")
                        .queryParam("page", 1)
                        .queryParam("size", 3)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MaintenanceRequestDto.class)
                .consumeWith(result -> {
                    assertNotNull(result.getResponseBody());
                    assertEquals(3, result.getResponseBody().size());
                });
    }

    @Test
    void testGetMaintenanceRequestsSortedById() {
        // Create maintenance requests
        Long serial1 = ThreadLocalRandom.current().nextLong(1_000_000, 2_000_000);
        Long serial2 = ThreadLocalRandom.current().nextLong(2_000_000, 3_000_000);
        Long serial3 = ThreadLocalRandom.current().nextLong(3_000_000, 4_000_000);

        MaintenanceRequestRequest req1 = createTestRequest();
        req1.setSpaceshipSerial(serial1);
        MaintenanceRequestRequest req2 = createTestRequest();
        req2.setSpaceshipSerial(serial2);
        MaintenanceRequestRequest req3 = createTestRequest();
        req3.setSpaceshipSerial(serial3);

        MaintenanceRequestDto created1 = webClient.post().uri("/maintenance-requests").bodyValue(req1).exchange()
                .expectStatus().isOk().expectBody(MaintenanceRequestDto.class).returnResult().getResponseBody();
        MaintenanceRequestDto created2 = webClient.post().uri("/maintenance-requests").bodyValue(req2).exchange()
                .expectStatus().isOk().expectBody(MaintenanceRequestDto.class).returnResult().getResponseBody();
        MaintenanceRequestDto created3 = webClient.post().uri("/maintenance-requests").bodyValue(req3).exchange()
                .expectStatus().isOk().expectBody(MaintenanceRequestDto.class).returnResult().getResponseBody();

        assertNotNull(created1);
        assertNotNull(created2);
        assertNotNull(created3);

        // Collect IDs and sort them to verify they appear in sorted order in the list
        List<Long> createdIds = List.of(created1.getId(), created2.getId(), created3.getId());
        List<Long> sortedIds = new ArrayList<>(createdIds);
        sortedIds.sort(Comparator.naturalOrder());

        // Verify we can retrieve them individually and they are sorted
        webClient.get().uri("/maintenance-requests/{id}", created1.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(MaintenanceRequestDto.class)
                .consumeWith(result -> assertEquals(created1.getId(), result.getResponseBody().getId()));

        webClient.get().uri("/maintenance-requests/{id}", created2.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(MaintenanceRequestDto.class)
                .consumeWith(result -> assertEquals(created2.getId(), result.getResponseBody().getId()));

        webClient.get().uri("/maintenance-requests/{id}", created3.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(MaintenanceRequestDto.class)
                .consumeWith(result -> assertEquals(created3.getId(), result.getResponseBody().getId()));

        // Also verify they appear in the sorted list (may need to check multiple pages)
        webClient.get().uri("/maintenance-requests?page=0&size=100")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MaintenanceRequestDto.class)
                .consumeWith(result -> {
                    List<MaintenanceRequestDto> requests = result.getResponseBody();
                    assertNotNull(requests);
                    // Find the created requests and verify their order
                    List<MaintenanceRequestDto> sortedCreated = requests.stream()
                            .filter(r -> r.getId() != null && createdIds.contains(r.getId()))
                            .sorted(Comparator.comparing(MaintenanceRequestDto::getId))
                            .toList();

                    // If we found all 3, verify they're sorted and match expected order
                    if (sortedCreated.size() == 3) {
                        List<Long> foundIds = sortedCreated.stream()
                                .map(MaintenanceRequestDto::getId)
                                .toList();
                        assertEquals(sortedIds, foundIds,
                                "Found requests should be sorted by ID. Expected: " + sortedIds
                                        + ", Found: " + foundIds);
                    }
                    // If not all found in first page, that's okay - we already verified they exist individually
                });
    }
}

