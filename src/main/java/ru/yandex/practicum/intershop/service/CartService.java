package ru.yandex.practicum.intershop.service;

import ru.yandex.practicum.intershop.dto.Action;
import ru.yandex.practicum.intershop.dto.ItemDto;

import java.util.List;

public interface CartService {

    List<ItemDto> getCart();

    void changeItemCountInCartByItemId(Long itemId, Action action);
}
