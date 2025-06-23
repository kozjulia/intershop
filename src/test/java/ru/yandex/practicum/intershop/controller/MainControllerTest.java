package ru.yandex.practicum.intershop.controller;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import ru.yandex.practicum.intershop.BaseIntegrationTest;

import static org.apache.logging.log4j.util.Strings.EMPTY;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static ru.yandex.practicum.intershop.TestConstants.ITEM_ID;

class MainControllerTest extends BaseIntegrationTest {

    @Test
    @SneakyThrows
    void getMainPage_shouldReturnHtmlWithMainTest() {
        var result = webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/main/items")
                        .queryParam("search", EMPTY)
                        .queryParam("sort", "NO")
                        .build())
                .accept(MediaType.TEXT_HTML)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_HTML)
                .expectBody()
                .returnResult();


        MockMvcWebTestClient.resultActionsFor(result)
                .andExpect(view().name("main"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("search"))
                .andExpect(model().attributeExists("sort"))
                .andExpect(model().attributeExists("paging"));
    }

    @Test
    @SneakyThrows
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