package ru.yandex.practicum.intershop.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.test.context.ContextConfiguration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.yandex.practicum.intershop.TestConstants;
import ru.yandex.practicum.intershop.model.ItemEntity;

import static ru.yandex.practicum.intershop.TestConstants.ITEM_COUNT;
import static ru.yandex.practicum.intershop.TestConstants.ITEM_DESCRIPTION;
import static ru.yandex.practicum.intershop.TestConstants.ITEM_IMAGE_PATH;
import static ru.yandex.practicum.intershop.TestConstants.ITEM_PRICE;
import static ru.yandex.practicum.intershop.TestConstants.ITEM_TITLE;

@DataR2dbcTest
@ContextConfiguration(classes = {ItemRepository.class})
class ItemRepositoryTest {

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
        // itemRepository.save(itemEntity);

    }

/*    @Test
    void searchAllPagingAndSortingSuccessfulTest() {
        int pageSize = 10;
        String sortColumn = "id";

        Flux<ItemEntity> items = itemRepository.searchAllPagingAndSorting(EMPTY, sortColumn, pageSize, 0);

        assertThat(items.count(), equalTo(1));
        assertThat(items.blockFirst(), equalTo(itemEntity));
    }*/

    @Test
    void findAllByIdInSuccessfulTest() {
        Mono<ItemEntity> saveAndFind = itemRepository.save(itemEntity)
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

   /* @Test
    void updateImagePathSuccessfulTest() {
        Long itemId = itemEntity.getId();
        String newImagePath = "new imagePath";

        itemRepository.updateImagePath(itemId, newImagePath);

        Mono<ItemEntity> updatedItem = itemRepository.findById(itemId);

        assertThat(updatedItem.block().getImgPath(), equalTo(newImagePath));
    }
*/
/*    @Test
    void updateCountItemSuccessfulTest() {
        Long itemId = itemEntity.getId();
        Integer minusCount = 3;

        itemRepository.updateCountItem(itemId, minusCount);

        Mono<ItemEntity> updatedItem = itemRepository.findById(itemId);

        assertThat(updatedItem.block().getCount(), equalTo(ITEM_COUNT - minusCount));
    }*/

    @Test
    void findAllByIdTest() {
        Mono<ItemEntity> save = itemRepository.save(itemEntity);
        Flux<ItemEntity> findAll = save.flatMapMany(saved -> itemRepository.findAllById(java.util.List.of(saved.getId())));
        StepVerifier.create(findAll)
                .expectNextMatches(item -> item.getTitle().equals(TestConstants.ITEM_TITLE))
                .verifyComplete();
    }
}