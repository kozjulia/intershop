package ru.yandex.practicum.intershop.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.dto.Action;
import ru.yandex.practicum.intershop.dto.CartItemDto;
import ru.yandex.practicum.intershop.dto.ItemDto;

public interface CartService {

    Flux<ItemDto> getCart();

    Mono<Void> changeItemCountInCartByItemId(Long itemId, Action action);

    Flux<CartItemDto> getAndResetCart();
}
