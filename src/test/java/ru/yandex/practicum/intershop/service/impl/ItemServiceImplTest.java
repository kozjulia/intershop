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
import ru.yandex.practicum.intershop.TestConstants;
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
        when(itemRepository.findById(TestConstants.ITEM_ID)).thenReturn(Mono.just(TestConstants.ITEM_ENTITY));
        when(itemMapper.toItemDto(TestConstants.ITEM_ENTITY)).thenReturn(ITEM_DTO);

        StepVerifier.create(itemService.getItemById(TestConstants.ITEM_ID))
                .expectNext(ITEM_DTO)
                .verifyComplete();

        verify(itemRepository).findById(TestConstants.ITEM_ID);
        verify(itemMapper).toItemDto(TestConstants.ITEM_ENTITY);
    }

    @Test
    void getItemImageByImageWithWrongImagePathPathSuccessfulTest() {
        ReflectionTestUtils.setField(itemService, "pathForUploadImage", "uploads");

        StepVerifier.create(itemService.getItemImageByImagePath(ITEM_IMAGE_PATH))
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

        StepVerifier.create(itemService.findAllItemsPagingAndSorting(EMPTY, ItemSort.NO, pageSize, pageNumber))
                .expectNext(ITEM_DTO)
                .verifyComplete();

        verify(itemRepository).searchAllPagingAndSorting(EMPTY, sortColumn, pageSize, offset);
        verify(itemMapper).toItemDto(ITEM_ENTITY);
    }

    @Test
    void findAllItemsByIdsSuccessfulTest() {
        when(itemRepository.findAllById(List.of(ITEM_ID)))
                .thenReturn(Flux.just(ITEM_ENTITY));
        when(itemMapper.toItemDto(ITEM_ENTITY))
                .thenReturn(ITEM_DTO);

        StepVerifier.create(itemService.findAllItemsByIds(List.of(TestConstants.ITEM_ID)))
                .expectNext(ITEM_DTO)
                .verifyComplete();

        verify(itemRepository).findAllById(java.util.List.of(TestConstants.ITEM_ID));
        verify(itemMapper).toItemDto(TestConstants.ITEM_ENTITY);
    }

    @Test
    void deleteItemSuccessfulTest() {
        when(itemRepository.deleteById(ITEM_ID))
                .thenReturn(Mono.empty());

        StepVerifier.create(itemService.deleteItem(TestConstants.ITEM_ID))
                .verifyComplete();

        verify(itemRepository).deleteById(TestConstants.ITEM_ID);
    }

    @Test
    void updateItemSuccessfulTest() {
        CartItemDto cartItemDto = CartItemDto.builder()
                .itemId(ITEM_ID)
                .count(ITEM_COUNT)
                .build();
        when(itemRepository.findById(ITEM_ID))
                .thenReturn(Mono.just(ITEM_ENTITY));
        when(itemRepository.save(ITEM_ENTITY))
                .thenReturn(Mono.just(ITEM_ENTITY));

        StepVerifier.create(itemService.updateItem(cartItemDto))
                .verifyComplete();

        verify(itemRepository).findById(ITEM_ID);
        verify(itemRepository).save(ITEM_ENTITY);
    }
}