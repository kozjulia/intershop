package ru.yandex.practicum.intershop.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import ru.yandex.practicum.intershop.BaseIntegrationTest;

import static org.apache.logging.log4j.util.Strings.EMPTY;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.yandex.practicum.intershop.TestConstants.ITEM_ID;

class MainControllerTest extends BaseIntegrationTest {

    @Test
    void getMainPage_shouldReturnHtmlWithMainTest() {
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/main/items")
                        .queryParam("search", EMPTY)
                        .queryParam("sort", "NO")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.TEXT_HTML)
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("<form"));
                });
    }

    @Test
    void changeItemCountInCart_shouldRedirectTest() {
        webTestClient
                .post()
                .uri(uriBuilder -> uriBuilder.path("/main/items/" + ITEM_ID)
                        .queryParam("action", "plus")
                        .build())
                .accept(MediaType.TEXT_HTML)
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().location("/main/items");
    }
}