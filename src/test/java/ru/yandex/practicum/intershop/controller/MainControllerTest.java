package ru.yandex.practicum.intershop.controller;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.intershop.BaseIntegrationTest;

import static org.apache.logging.log4j.util.Strings.EMPTY;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static ru.yandex.practicum.intershop.TestConstants.ITEM_ID;

class MainControllerTest extends BaseIntegrationTest {

    @Test
    @SneakyThrows
    void getMainPage_shouldReturnHtmlWithMainTest() {
        mockMvc.perform(get("/main/items")
                        .param("search", EMPTY)
                        .param("sort", "NO"))
                .andExpect(status().isOk())
                .andExpect(view().name("main"))
                .andExpect(model().attributeExists("items", "search", "sort", "paging"));
    }

    @Test
    @SneakyThrows
    void changeItemCountInCart_shouldRedirectTest() {
        mockMvc.perform(post("/main/items/" + ITEM_ID)
                        .param("action", "plus"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/main/items"));
    }
}