package ru.yandex.practicum.store.showcase.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.test.StepVerifier;
import ru.yandex.practicum.store.showcase.BaseIntegrationTest;
import ru.yandex.practicum.store.showcase.TestConstants;
import ru.yandex.practicum.store.showcase.repository.ItemRepository;

import java.nio.charset.StandardCharsets;

class ItemControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ItemRepository itemRepository;

    @Test
    @WithMockUser
    void addItem_shouldAddItemToDatabaseAndRedirectTest() {

        byte[] fakeImageBytes = "fake-image-content".getBytes(StandardCharsets.UTF_8);
        ByteArrayResource imageResource = new ByteArrayResource(fakeImageBytes) {
            @Override
            public String getFilename() {
                return "test.jpg";
            }
        };

        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("title", TestConstants.ITEM_TITLE);
        formData.add("description", TestConstants.ITEM_DESCRIPTION);
        formData.add("count", TestConstants.ITEM_COUNT.toString());
        formData.add("price", TestConstants.ITEM_PRICE.toString());
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
    @WithMockUser
    void deleteItem_shouldRemoveItemFromDatabaseAndRedirectTest() {
        webTestClient.post()
                .uri("/items/" + TestConstants.ITEM_ID + "/delete")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().location("/main/items");

        itemRepository.findById(TestConstants.ITEM_ID)
                .as(StepVerifier::create)
                .verifyComplete();
    }
}