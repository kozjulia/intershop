package ru.yandex.practicum.intershop.controller;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.test.StepVerifier;
import ru.yandex.practicum.intershop.BaseIntegrationTest;
import ru.yandex.practicum.intershop.model.ItemEntity;
import ru.yandex.practicum.intershop.repository.ItemRepository;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.yandex.practicum.intershop.TestConstants.ITEM_COUNT;
import static ru.yandex.practicum.intershop.TestConstants.ITEM_DESCRIPTION;
import static ru.yandex.practicum.intershop.TestConstants.ITEM_ID;
import static ru.yandex.practicum.intershop.TestConstants.ITEM_PRICE;
import static ru.yandex.practicum.intershop.TestConstants.ITEM_TITLE;

class ItemControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ItemRepository itemRepository;

    @Test
    @SneakyThrows
    void addItem_shouldAddItemToDatabaseAndRedirectTest() {
        MultipartBodyBuilder image = new MultipartBodyBuilder();
        image.part("title", ITEM_TITLE);
        image.part("description", ITEM_DESCRIPTION);
        image.part("count", ITEM_COUNT);
        image.part("price", ITEM_PRICE);
        image.part("image", new ClassPathResource("test.jpg"), MediaType.IMAGE_JPEG);

        webTestClient.post()
                .uri("/items")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(image.build()))
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().location("/items/1");

        StepVerifier.create(itemRepository.count())
                .expectNextMatches(count -> count == 1)
                .verifyComplete();
    }

    @Test
    @SneakyThrows
    void deleteItem_shouldRemoveItemFromDatabaseAndRedirectTest() {
        webTestClient.post()
                .uri("/items/" + ITEM_ID + "/delete")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().location("/main/items");

        itemRepository.findById(ITEM_ID)
                .as(StepVerifier::create)
                .verifyComplete();
    }
}