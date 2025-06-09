package ru.yandex.practicum.intershop.controller;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.yandex.practicum.intershop.BaseIntegrationTest;
import ru.yandex.practicum.intershop.model.ItemEntity;

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

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @SneakyThrows
    void addItem_shouldAddItemToDatabaseAndRedirectTest() {
        MockMultipartFile image = new MockMultipartFile(
                "image", "test.jpg", "image/jpeg", "IMAGE".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/items")
                        .file(image)
                        .param("title", ITEM_TITLE)
                        .param("description", ITEM_DESCRIPTION)
                        .param("count", ITEM_COUNT.toString())
                        .param("price", ITEM_PRICE.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items/1"));

        List<ItemEntity> items = entityManager.createQuery("FROM ItemEntity", ItemEntity.class)
                .getResultList();

        assertThat(items.size(), equalTo(1));

        ItemEntity resultItem = items.getFirst();
        assertThat(resultItem.getId(), equalTo(1L));
        assertThat(resultItem.getTitle(), equalTo(ITEM_TITLE));
        assertThat(resultItem.getDescription(), equalTo(ITEM_DESCRIPTION));
        assertThat(resultItem.getImgPath(), equalTo("item-image-1.jpg"));
        assertThat(resultItem.getCount(), equalTo(ITEM_COUNT));
        assertThat(resultItem.getPrice().compareTo(ITEM_PRICE), equalTo(0));
    }

    @Test
    @SneakyThrows
    void deleteItem_shouldRemoveItemFromDatabaseAndRedirectTes() {
        mockMvc.perform(post("/items/" + ITEM_ID + "/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/main/items"));
    }
}