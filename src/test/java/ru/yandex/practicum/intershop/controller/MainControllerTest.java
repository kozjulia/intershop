package ru.yandex.practicum.intershop.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class MainControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void getMainPage_shouldReturnHtmlWithMainTest() {
        webTestClient.get()
                .uri("/main/items?search=&sort=NO")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void changeItemCountInCart_shouldRedirectTest() {
        webTestClient.post()
                .uri("/main/items/77?action=plus")
                .exchange()
                .expectStatus().is3xxRedirection();
    }
}