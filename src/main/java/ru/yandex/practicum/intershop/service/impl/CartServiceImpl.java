package ru.yandex.practicum.intershop.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import ru.yandex.practicum.intershop.dto.Action;
import ru.yandex.practicum.intershop.dto.CartItemDto;
import ru.yandex.practicum.intershop.dto.ItemDto;
import ru.yandex.practicum.intershop.exception.NotFoundException;
import ru.yandex.practicum.intershop.service.CartService;
import ru.yandex.practicum.intershop.service.ItemService;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
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
    public Flux<ItemDto> getCart() {

        List<Long> itemIds = new ArrayList<>(cart.keySet());

        return itemService.findAllItemsByIds(itemIds)
                .map(this::convertItemWithCartCount)
                .switchIfEmpty(Flux.empty());
    }

    @Override
    public Mono<Void> changeItemCountInCartByItemId(Long itemId, Action action) {

        return Mono.fromCallable(() -> {
                    switch (action) {
                        case PLUS:
                            cart.compute(itemId, (k, v) -> (isNull(v)) ? 1 : v + 1);
                            break;
                        case MINUS:
                            cart.compute(itemId, (k, v) -> (isNull(v) || v == 0) ? 0 : v - 1);
                            break;
                        case DELETE:
                            cart.remove(itemId);
                            break;
                        default:
                            throw new NotFoundException("Действия: " + action + " не существует");
                    }
                    return true; // просто для Void
                })
                .then();
    }

    @Override
    public Flux<CartItemDto> getAndResetCart() {

        return Mono.fromCallable(() -> {
                    List<CartItemDto> result = cart.entrySet()
                            .stream()
                            .map(entry -> CartItemDto.builder()
                                    .itemId(entry.getKey())
                                    .count(entry.getValue())
                                    .build())
                            .toList();

                    cart.clear();

                    return result;
                })
                .flatMapMany(Flux::fromIterable);
    }

    private ItemDto convertItemWithCartCount(ItemDto item) {

        Integer cartCount = cart.getOrDefault(item.getId(), 0);
        item.setCount(cartCount);

        return item;
    }
}
