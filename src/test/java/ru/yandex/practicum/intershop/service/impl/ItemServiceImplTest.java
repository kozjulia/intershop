package ru.yandex.practicum.intershop.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.yandex.practicum.intershop.dto.CartItemDto;
import ru.yandex.practicum.intershop.dto.ItemDto;
import ru.yandex.practicum.intershop.dto.ItemSort;
import ru.yandex.practicum.intershop.mapper.ItemMapper;
import ru.yandex.practicum.intershop.model.ItemEntity;
import ru.yandex.practicum.intershop.repository.ItemRepository;

import java.util.List;

import static org.apache.logging.log4j.util.Strings.EMPTY;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.yandex.practicum.intershop.TestConstants.ITEM_COUNT;
import static ru.yandex.practicum.intershop.TestConstants.ITEM_DESCRIPTION;
import static ru.yandex.practicum.intershop.TestConstants.ITEM_ENTITY;
import static ru.yandex.practicum.intershop.TestConstants.ITEM_ID;
import static ru.yandex.practicum.intershop.TestConstants.ITEM_IMAGE_PATH;
import static ru.yandex.practicum.intershop.TestConstants.ITEM_PRICE;
import static ru.yandex.practicum.intershop.TestConstants.ITEM_TITLE;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemMapper itemMapper;
    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private static final ItemDto ITEM_DTO = new ItemDto(
            ITEM_ID,
            ITEM_TITLE,
            ITEM_DESCRIPTION,
            ITEM_IMAGE_PATH,
            ITEM_COUNT,
            ITEM_PRICE);

    @Test
    void getItemByIdSuccessfulTest() {
        when(itemRepository.findById(ITEM_ID))
                .thenReturn(Mono.just(ITEM_ENTITY));
        when(itemMapper.toItemDto(ITEM_ENTITY))
                .thenReturn(ITEM_DTO);

        Mono<ItemDto> resultItem = itemService.getItemById(ITEM_ID);

        StepVerifier.create(resultItem)
                .expectNext(ITEM_DTO)
                .verifyComplete();
    }

    @Test
    void getItemImageByImageWithWrongImagePathPathSuccessfulTest() {
        ReflectionTestUtils.setField(itemService, "pathForUploadImage", "uploads");
        Mono<byte[]> resultImage = itemService.getItemImageByImagePath(ITEM_IMAGE_PATH);

        StepVerifier.create(resultImage)
                .expectNext(new byte[0])
                .verifyComplete();
    }

    @Test
    void findAllItemsPagingAndSortingSuccessfulTest() {
        int pageNumber = 1;
        int pageSize = 10;
        int offset = 0;
        String sortColumn = "id";
        Flux<ItemEntity> items = Flux.just(ITEM_ENTITY);

        when(itemRepository.searchAllPagingAndSorting(EMPTY, sortColumn, pageSize, offset))
                .thenReturn(items);
        when(itemMapper.toItemDto(ITEM_ENTITY))
                .thenReturn(ITEM_DTO);

        Flux<ItemDto> resultItems = itemService.findAllItemsPagingAndSorting(EMPTY, ItemSort.NO, pageSize, pageNumber);

        StepVerifier.create(resultItems)
                .expectNext(ITEM_DTO)
                .verifyComplete();
    }

    @Test
    void findAllItemsByIdsSuccessfulTest() {
        when(itemRepository.findAllByIdIn(List.of(ITEM_ID)))
                .thenReturn(Flux.just(ITEM_ENTITY));
        when(itemMapper.toItemDto(ITEM_ENTITY))
                .thenReturn(ITEM_DTO);

        Flux<ItemDto> resultItems = itemService.findAllItemsByIds(List.of(ITEM_ID));

        StepVerifier.create(resultItems)
                .expectNext(ITEM_DTO)
                .verifyComplete();
    }

    @Test
    void deleteItemSuccessfulTest() {
        when(itemRepository.deleteById(ITEM_ID))
                .thenReturn(Mono.empty());

        Mono<Void> result = itemService.deleteItem(ITEM_ID);

        StepVerifier.create(result)
                .verifyComplete();

        verify(itemRepository).deleteById(ITEM_ID);
    }

    @Test
    void updateItemSuccessfulTest() {
        CartItemDto cartItemDto = CartItemDto.builder()
                .itemId(ITEM_ID)
                .count(ITEM_COUNT)
                .build();

        when(itemRepository.updateCountItem(ITEM_ID, ITEM_COUNT))
                .thenReturn(Mono.empty());

        Mono<Void> result = itemService.updateItem(cartItemDto);

        StepVerifier.create(result)
                .verifyComplete();

        verify(itemRepository).updateCountItem(ITEM_ID, ITEM_COUNT);
    }
}