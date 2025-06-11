package ru.yandex.practicum.intershop.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.TestcontainersConfiguration;
import ru.yandex.practicum.intershop.model.ItemEntity;

import java.util.List;

import static org.apache.logging.log4j.util.Strings.EMPTY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static ru.yandex.practicum.intershop.TestConstants.ITEM_COUNT;
import static ru.yandex.practicum.intershop.TestConstants.ITEM_DESCRIPTION;
import static ru.yandex.practicum.intershop.TestConstants.ITEM_IMAGE_PATH;
import static ru.yandex.practicum.intershop.TestConstants.ITEM_PRICE;
import static ru.yandex.practicum.intershop.TestConstants.ITEM_TITLE;

@DataJpaTest
@Testcontainers
@Import(TestcontainersConfiguration.class)
@Sql(scripts = {"/truncate-tables.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ItemRepositoryTest {

    private ItemEntity itemEntity;

    @Autowired
    private ItemRepository itemRepository;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

    @BeforeEach
    public void init() {
        itemEntity = ItemEntity.builder()
                .title(ITEM_TITLE)
                .description(ITEM_DESCRIPTION)
                .imgPath(ITEM_IMAGE_PATH)
                .count(ITEM_COUNT)
                .price(ITEM_PRICE)
                .build();
        itemRepository.save(itemEntity);

    }

    @Test
    void searchAllPagingAndSortingSuccessfulTest() {
        int pageNumber = 1;
        int pageSize = 10;
        Pageable page = PageRequest.of(pageNumber - 1, pageSize);

        Page<ItemEntity> items = itemRepository.searchAllPagingAndSorting(EMPTY, page);

        assertThat(items.getContent().size(), equalTo(1));
        assertThat(items.getContent().getFirst(), equalTo(itemEntity));
    }

    @Test
    void findAllByIdInSuccessfulTest() {
        Long itemId = itemEntity.getId();

        List<ItemEntity> resultItems = itemRepository.findAllByIdIn(List.of(itemId));

        assertThat(resultItems.size(), equalTo(1));
        assertThat(resultItems.getFirst(), equalTo(itemEntity));
    }

    @Test
    void updateImagePathSuccessfulTest() {
        Long itemId = itemEntity.getId();
        String newImagePath = "new imagePath";

        itemRepository.updateImagePath(itemId, newImagePath);

        ItemEntity updatedItem = itemRepository.findById(itemId).get();

        assertThat(updatedItem.getImgPath(), equalTo(newImagePath));
    }

    @Test
    void updateCountItemSuccessfulTest() {
        Long itemId = itemEntity.getId();
        Integer minusCount = 3;

        itemRepository.updateCountItem(itemId, minusCount);

        ItemEntity updatedItem = itemRepository.findById(itemId).get();

        assertThat(updatedItem.getCount(), equalTo(ITEM_COUNT - minusCount));
    }
}