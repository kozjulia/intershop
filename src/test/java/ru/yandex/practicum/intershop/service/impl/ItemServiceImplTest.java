package ru.yandex.practicum.intershop.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.yandex.practicum.intershop.dto.CartItemDto;
import ru.yandex.practicum.intershop.dto.ItemDto;
import ru.yandex.practicum.intershop.mapper.ItemMapper;
import ru.yandex.practicum.intershop.repository.ItemRepository;
import ru.yandex.practicum.intershop.TestConstants;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemMapper itemMapper;
    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private static final ItemDto ITEM_DTO = new ItemDto(
            TestConstants.ITEM_ID,
            TestConstants.ITEM_TITLE,
            TestConstants.ITEM_DESCRIPTION,
            TestConstants.ITEM_IMAGE_PATH,
            TestConstants.ITEM_COUNT,
            TestConstants.ITEM_PRICE);

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
    void findAllItemsByIdsSuccessfulTest() {
        when(itemRepository.findAllById(java.util.List.of(TestConstants.ITEM_ID))).thenReturn(Flux.just(TestConstants.ITEM_ENTITY));
        when(itemMapper.toItemDto(TestConstants.ITEM_ENTITY)).thenReturn(ITEM_DTO);
        StepVerifier.create(itemService.findAllItemsByIds(java.util.List.of(TestConstants.ITEM_ID)))
                .expectNext(ITEM_DTO)
                .verifyComplete();
        verify(itemRepository).findAllById(java.util.List.of(TestConstants.ITEM_ID));
        verify(itemMapper).toItemDto(TestConstants.ITEM_ENTITY);
    }

    @Test
    void deleteItemSuccessfulTest() {
        when(itemRepository.deleteById(TestConstants.ITEM_ID)).thenReturn(Mono.empty());
        StepVerifier.create(itemService.deleteItem(TestConstants.ITEM_ID))
                .verifyComplete();
        verify(itemRepository).deleteById(TestConstants.ITEM_ID);
    }

    @Test
    void updateItemSuccessfulTest() {
        CartItemDto cartItemDto = CartItemDto.builder()
                .itemId(TestConstants.ITEM_ID)
                .count(TestConstants.ITEM_COUNT)
                .build();
        when(itemRepository.findById(TestConstants.ITEM_ID)).thenReturn(Mono.just(TestConstants.ITEM_ENTITY));
        when(itemRepository.save(TestConstants.ITEM_ENTITY)).thenReturn(Mono.just(TestConstants.ITEM_ENTITY));
        StepVerifier.create(itemService.updateItem(cartItemDto))
                .verifyComplete();
        verify(itemRepository).findById(TestConstants.ITEM_ID);
        verify(itemRepository).save(TestConstants.ITEM_ENTITY);
    }
}