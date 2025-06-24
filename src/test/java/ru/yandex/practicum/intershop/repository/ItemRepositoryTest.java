package ru.yandex.practicum.intershop.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.test.context.ContextConfiguration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.yandex.practicum.intershop.model.Items;
import ru.yandex.practicum.intershop.TestConstants;

@DataR2dbcTest
@ContextConfiguration(classes = {ItemRepository.class})
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    private Items items;

    @BeforeEach
    public void init() {
        items = Items.builder()
                .title(TestConstants.ITEM_TITLE)
                .description(TestConstants.ITEM_DESCRIPTION)
                .imgPath(TestConstants.ITEM_IMAGE_PATH)
                .count(TestConstants.ITEM_COUNT)
                .price(TestConstants.ITEM_PRICE)
                .build();
    }

    @Test
    void saveAndFindByIdTest() {
        Mono<Items> saveAndFind = itemRepository.save(items)
                .flatMap(saved -> itemRepository.findById(saved.getId()));
        StepVerifier.create(saveAndFind)
                .expectNextMatches(item ->
                        item.getTitle().equals(TestConstants.ITEM_TITLE)
                        && item.getDescription().equals(TestConstants.ITEM_DESCRIPTION)
                        && item.getImgPath().equals(TestConstants.ITEM_IMAGE_PATH)
                        && item.getCount().equals(TestConstants.ITEM_COUNT)
                        && item.getPrice().equals(TestConstants.ITEM_PRICE)
                )
                .verifyComplete();
    }

    @Test
    void findAllByIdTest() {
        Mono<Items> save = itemRepository.save(items);
        Flux<Items> findAll = save.flatMapMany(saved -> itemRepository.findAllById(java.util.List.of(saved.getId())));
        StepVerifier.create(findAll)
                .expectNextMatches(item -> item.getTitle().equals(TestConstants.ITEM_TITLE))
                .verifyComplete();
    }
}