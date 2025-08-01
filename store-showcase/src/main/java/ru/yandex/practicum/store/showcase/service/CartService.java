package ru.yandex.practicum.store.showcase.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.store.showcase.dto.Action;
import ru.yandex.practicum.store.showcase.dto.CartItemDto;
import ru.yandex.practicum.store.showcase.dto.ItemDto;

public interface CartService {

    Flux<ItemDto> getCart();

    Mono<Void> changeItemCountInCartByItemId(Long itemId, Action action);

    Flux<CartItemDto> getAndResetCart();
}
