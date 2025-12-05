package ru.itmo.spaceships.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.itmo.spaceships.BaseDbTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PingControllerTest extends BaseDbTest {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    WebTestClient webClient;

    @Test
    void pingTest() {
        String url = "http://localhost:" + port + "/ping";
        String response = restTemplate.getForObject(url, String.class);

        assertEquals("pong", response);
    }

    @Test
    void reactivePingTest() {
        webClient.get().uri("/ping")
                .exchange()
                .expectStatus().isOk()
                .expectBody().consumeWith(result ->
                        assertEquals("pong", new String(result.getResponseBody()))
                );
    }
}