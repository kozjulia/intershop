package ru.yandex.practicum.intershop.service;

import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.dto.Action;
import ru.yandex.practicum.intershop.dto.CartItemDto;
import ru.yandex.practicum.intershop.dto.ItemDto;

import java.util.List;

public interface CartService {

    Mono<List<ItemDto>> getCart();

    Mono<Void> changeItemCountInCartByItemId(Long itemId, Action action);

    Mono<List<CartItemDto>> getAndResetCart();
}
