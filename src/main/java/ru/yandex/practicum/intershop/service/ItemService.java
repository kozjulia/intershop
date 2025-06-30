package ru.yandex.practicum.intershop.service;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.dto.CartItemDto;
import ru.yandex.practicum.intershop.dto.ItemDto;
import ru.yandex.practicum.intershop.dto.ItemSort;

import java.math.BigDecimal;
import java.util.List;

public interface ItemService {

    Mono<ItemDto> getItemById(Long itemId);

    Mono<byte[]> getItemImageByImagePath(String imagePath);

    Flux<ItemDto> findAllItemsPagingAndSorting(String search, ItemSort itemSort, Integer pageSize, Integer pageNumber);

    Flux<ItemDto> findAllItemsByIds(List<Long> itemIds);

    Mono<Long> addItem(String title, String description, FilePart image, Integer count, BigDecimal price);

    Mono<Void> editItem(Long itemId, String title, String description, FilePart image, Integer count, BigDecimal price);

    Mono<Void> deleteItem(Long itemId);

    Mono<Void> updateItem(CartItemDto cartItemDto);
}
