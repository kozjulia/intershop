package ru.yandex.practicum.intershop.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.yandex.practicum.intershop.dto.ItemDto;
import ru.yandex.practicum.intershop.mapper.ItemMapper;
import ru.yandex.practicum.intershop.model.ItemEntity;
import ru.yandex.practicum.intershop.repository.ItemRepository;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.yandex.practicum.intershop.TestConstants.ITEM_COUNT;
import static ru.yandex.practicum.intershop.TestConstants.ITEM_DESCRIPTION;
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

    private static final ItemEntity ITEM_ENTITY = ItemEntity.builder()
            .id(ITEM_ID)
            .title(ITEM_TITLE)
            .description(ITEM_DESCRIPTION)
            .imgPath(ITEM_IMAGE_PATH)
            .count(ITEM_COUNT)
            .price(ITEM_PRICE)
            .build();
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
    void findAllItemsPagingAndSorting() {
    }

    @Test
    void findAllItemsByIds() {
    }

    @Test
    void addItem() {
    }

    @Test
    void editItem() {
    }

    @Test
    void deleteItem() {
    }

    @Test
    void updateItem() {
    }
}