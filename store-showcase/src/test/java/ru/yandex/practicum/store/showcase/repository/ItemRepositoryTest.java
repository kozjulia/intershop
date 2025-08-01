package ru.yandex.practicum.store.showcase.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.yandex.practicum.store.showcase.PostreSqlTestcontainer;
import ru.yandex.practicum.store.showcase.TestConstants;
import ru.yandex.practicum.store.showcase.model.ItemEntity;

import java.util.List;

import static org.apache.logging.log4j.util.Strings.EMPTY;

@DataR2dbcTest
@Testcontainers
@ImportTestcontainers(PostreSqlTestcontainer.class)
class ItemRepositoryTest {

    private ItemEntity itemEntity;

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    public void init() {
        itemRepository.deleteAll()
                .block();

        itemEntity = ItemEntity.builder()
                .title(TestConstants.ITEM_TITLE)
                .description(TestConstants.ITEM_DESCRIPTION)
                .imgPath(TestConstants.ITEM_IMAGE_PATH)
                .count(TestConstants.ITEM_COUNT)
                .price(TestConstants.ITEM_PRICE)
                .build();
    }

    @Test
    void searchAllPagingAndSortingSuccessfulTest() {
        int pageSize = 10;
        String sortColumn = "id";

        Flux<ItemEntity> foundItems = itemRepository.save(itemEntity)
                .thenMany(itemRepository.searchAllPagingAndSorting(EMPTY, sortColumn, pageSize, 0));

        StepVerifier.create(foundItems)
                .expectNextMatches(item ->
                        item.getTitle().equals(TestConstants.ITEM_TITLE)
                                && item.getDescription().equals(TestConstants.ITEM_DESCRIPTION)
                                && item.getImgPath().equals(TestConstants.ITEM_IMAGE_PATH)
                                && item.getCount().equals(TestConstants.ITEM_COUNT)
                                && (item.getPrice().compareTo(TestConstants.ITEM_PRICE) == 0))
                .verifyComplete();
    }

    @Test
    void findAllByIdInSuccessfulTest() {
        Mono<ItemEntity> savedAndFoundItem = itemRepository.save(itemEntity)
                .flatMap(saved -> itemRepository.findById(saved.getId()));

        StepVerifier.create(savedAndFoundItem)
                .expectNextMatches(item ->
                        item.getTitle().equals(TestConstants.ITEM_TITLE)
                                && item.getDescription().equals(TestConstants.ITEM_DESCRIPTION)
                                && item.getImgPath().equals(TestConstants.ITEM_IMAGE_PATH)
                                && item.getCount().equals(TestConstants.ITEM_COUNT)
                                && (item.getPrice().compareTo(TestConstants.ITEM_PRICE) == 0)
                )
                .verifyComplete();
    }

    @Test
    void findAllByIdSuccessfulTest() {
        Mono<ItemEntity> savedItem = itemRepository.save(itemEntity);
        Flux<ItemEntity> foundAllItems = savedItem.flatMapMany(saved -> itemRepository.findAllById(List.of(saved.getId())));

        StepVerifier.create(foundAllItems)
                .expectNextMatches(item ->
                        item.getTitle().equals(TestConstants.ITEM_TITLE)
                                && item.getDescription().equals(TestConstants.ITEM_DESCRIPTION)
                                && item.getImgPath().equals(TestConstants.ITEM_IMAGE_PATH)
                                && item.getCount().equals(TestConstants.ITEM_COUNT)
                                && (item.getPrice().compareTo(TestConstants.ITEM_PRICE) == 0))
                .verifyComplete();
    }
}