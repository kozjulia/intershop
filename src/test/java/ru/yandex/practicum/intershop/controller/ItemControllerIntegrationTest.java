package ru.yandex.practicum.intershop.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.test.StepVerifier;
import ru.yandex.practicum.intershop.BaseIntegrationTest;
import ru.yandex.practicum.intershop.repository.ItemRepository;

import java.nio.charset.StandardCharsets;

import static ru.yandex.practicum.intershop.TestConstants.ITEM_COUNT;
import static ru.yandex.practicum.intershop.TestConstants.ITEM_DESCRIPTION;
import static ru.yandex.practicum.intershop.TestConstants.ITEM_ID;
import static ru.yandex.practicum.intershop.TestConstants.ITEM_PRICE;
import static ru.yandex.practicum.intershop.TestConstants.ITEM_TITLE;

class ItemControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void addItem_shouldAddItemToDatabaseAndRedirectTest() {

        byte[] fakeImageBytes = "fake-image-content".getBytes(StandardCharsets.UTF_8);
        ByteArrayResource imageResource = new ByteArrayResource(fakeImageBytes) {
            @Override
            public String getFilename() {
                return "test.jpg";
            }
        };

        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("title", ITEM_TITLE);
        formData.add("description", ITEM_DESCRIPTION);
        formData.add("count", ITEM_COUNT.toString());
        formData.add("price", ITEM_PRICE.toString());
        formData.add("image", imageResource); // добавляем как FilePart

        webTestClient.post()
                .uri("/items")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(formData))
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().location("/items/1");

        StepVerifier.create(itemRepository.count())
                .expectNextMatches(count -> count == 1)
                .verifyComplete();
    }

    @Test
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