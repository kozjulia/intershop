package ru.yandex.practicum.intershop.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.intershop.dto.Action;
import ru.yandex.practicum.intershop.dto.CartItemDto;
import ru.yandex.practicum.intershop.dto.ItemDto;
import ru.yandex.practicum.intershop.exception.NotFoundException;
import ru.yandex.practicum.intershop.service.CartService;
import ru.yandex.practicum.intershop.service.ItemService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final Map<Long, Integer> cart = new ConcurrentHashMap<>();

    private final ItemService itemService;

    @Override
    public List<ItemDto> getCart() {

        return itemService.findAllItemsByIds(cart.keySet().stream().toList())
                .stream()
                .map(this::convertItemWithCartCount)
                .toList();
    }

    @Override
    public void changeItemCountInCartByItemId(Long itemId, Action action) {

        switch (action) {
            case PLUS -> cart.compute(itemId, (k, v) -> isNull(v) ? 1 : v + 1);
            case MINUS -> cart.compute(itemId, (k, v) -> (isNull(v) || v == 0) ? 0 : v - 1);
            case DELETE -> cart.remove(itemId);
            default -> new NotFoundException("Действия: " + action + " не существует");
        }
    }

    @Override
    public List<CartItemDto> getAndResetCart() {

        List<CartItemDto> cartItemDtos = cart.entrySet()
                .stream()
                .map(entry -> CartItemDto.builder()
                        .itemId(entry.getKey())
                        .count(entry.getValue())
                        .build())
                .toList();

        cart.clear();

        return cartItemDtos;
    }

    private ItemDto convertItemWithCartCount(ItemDto item) {

        Integer cartCount = cart.getOrDefault(item.getId(), 0);
        item.setCount(cartCount);

        return item;
    }
}
