package ru.yandex.practicum.intershop.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.BaseIntegrationTest;
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

class ItemRepositoryTest extends BaseIntegrationTest {

    private ItemEntity itemEntity;

    @Autowired
    private ItemRepository itemRepository;

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
        int pageSize = 10;
        String sortColumn = "id";

        Flux<ItemEntity> items = itemRepository.searchAllPagingAndSorting(EMPTY, sortColumn, pageSize, 0);

        assertThat(items.count(), equalTo(1));
        assertThat(items.blockFirst(), equalTo(itemEntity));
    }

    @Test
    void findAllByIdInSuccessfulTest() {
        Long itemId = itemEntity.getId();

        Flux<ItemEntity> resultItems = itemRepository.findAllByIdIn(List.of(itemId));

        assertThat(resultItems.count(), equalTo(1));
        assertThat(resultItems.blockFirst(), equalTo(itemEntity));
    }

    @Test
    void updateImagePathSuccessfulTest() {
        Long itemId = itemEntity.getId();
        String newImagePath = "new imagePath";

        itemRepository.updateImagePath(itemId, newImagePath);

        Mono<ItemEntity> updatedItem = itemRepository.findById(itemId);

        assertThat(updatedItem.block().getImgPath(), equalTo(newImagePath));
    }

    @Test
    void updateCountItemSuccessfulTest() {
        Long itemId = itemEntity.getId();
        Integer minusCount = 3;

        itemRepository.updateCountItem(itemId, minusCount);

        Mono<ItemEntity> updatedItem = itemRepository.findById(itemId);

        assertThat(updatedItem.block().getCount(), equalTo(ITEM_COUNT - minusCount));
    }
}