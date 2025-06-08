package ru.yandex.practicum.intershop.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import ru.yandex.practicum.intershop.dto.CartItemDto;
import ru.yandex.practicum.intershop.dto.ItemDto;
import ru.yandex.practicum.intershop.dto.ItemSort;
import ru.yandex.practicum.intershop.mapper.ItemMapper;
import ru.yandex.practicum.intershop.model.ItemEntity;
import ru.yandex.practicum.intershop.repository.ItemRepository;

import java.util.List;
import java.util.Optional;

import static org.apache.logging.log4j.util.Strings.EMPTY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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
                .thenReturn(Optional.of(ITEM_ENTITY));
        when(itemMapper.toItemDto(ITEM_ENTITY))
                .thenReturn(ITEM_DTO);

        ItemDto resultItem = itemService.getItemById(ITEM_ID);

        verify(itemRepository).findById(ITEM_ID);
        verify(itemMapper).toItemDto(ITEM_ENTITY);
        assertThat(resultItem, equalTo(ITEM_DTO));
    }

    @Test
    void getItemImageByImageWithWrongImagePathPathSuccessfulTest() {
        ReflectionTestUtils.setField(itemService, "pathForUploadImage", "uploads");
        byte[] resultImage = itemService.getItemImageByImagePath(ITEM_IMAGE_PATH);

        assertThat(resultImage, equalTo(new byte[0]));
    }

    @Test
    void findAllItemsPagingAndSortingSuccessfulTest() {
        int pageNumber = 1;
        int pageSize = 10;
        Pageable page = PageRequest.of(pageNumber - 1, pageSize);
        List<ItemEntity> itemList = List.of(ITEM_ENTITY);
        Page<ItemEntity> items = new PageImpl<>(itemList, page, itemList.size());

        when(itemRepository.searchAllPagingAndSorting(EMPTY, page))
                .thenReturn(items);
        when(itemMapper.toItemDto(ITEM_ENTITY))
                .thenReturn(ITEM_DTO);

        List<ItemDto> resultItems = itemService.findAllItemsPagingAndSorting(EMPTY, ItemSort.NO, pageSize, pageNumber);

        verify(itemRepository).searchAllPagingAndSorting(EMPTY, page);
        verify(itemMapper).toItemDto(ITEM_ENTITY);
        assertThat(resultItems.size(), equalTo(1));
        assertThat(resultItems.getFirst(), equalTo(ITEM_DTO));
    }

    @Test
    void findAllItemsByIdsSuccessfulTest() {
        when(itemRepository.findAllByIdIn(List.of(ITEM_ID)))
                .thenReturn(List.of(ITEM_ENTITY));
        when(itemMapper.toItemDto(ITEM_ENTITY))
                .thenReturn(ITEM_DTO);

        List<ItemDto> resultItems = itemService.findAllItemsByIds(List.of(ITEM_ID));

        verify(itemRepository).findAllByIdIn(List.of(ITEM_ID));
        verify(itemMapper).toItemDto(ITEM_ENTITY);
        assertThat(resultItems.size(), equalTo(1));
        assertThat(resultItems.getFirst(), equalTo(ITEM_DTO));
    }

    @Test
    void deleteItemSuccessfulTest() {
        assertDoesNotThrow(() -> itemService.deleteItem(ITEM_ID));

        verify(itemRepository).deleteById(ITEM_ID);
    }

    @Test
    void updateItemSuccessfulTest() {
        CartItemDto cartItemDto = CartItemDto.builder()
                .itemId(ITEM_ID)
                .count(ITEM_COUNT)
                .build();

        assertDoesNotThrow(() -> itemService.updateItem(cartItemDto));

        verify(itemRepository).updateCountItem(ITEM_ID, ITEM_COUNT);
    }
}