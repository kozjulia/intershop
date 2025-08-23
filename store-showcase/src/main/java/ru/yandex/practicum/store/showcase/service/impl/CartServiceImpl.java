package ru.yandex.practicum.store.showcase.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import ru.yandex.practicum.store.showcase.client.api.PaymentApi;
import ru.yandex.practicum.store.showcase.client.model.BalanceResponse;
import ru.yandex.practicum.store.showcase.dto.Action;
import ru.yandex.practicum.store.showcase.dto.CartItemDto;
import ru.yandex.practicum.store.showcase.dto.ItemDto;
import ru.yandex.practicum.store.showcase.exception.NotFoundException;
import ru.yandex.practicum.store.showcase.repository.ItemRepository;
import ru.yandex.practicum.store.showcase.service.CartService;
import ru.yandex.practicum.store.showcase.service.ItemService;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.store.showcase.service.OAuth2Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.isNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final Map<Long, Integer> cart = new ConcurrentHashMap<>();

    private final PaymentApi paymentApi;
    private final ItemService itemService;
    private final OAuth2Service oAuth2Service;
    private final ItemRepository itemRepository;

    @Override
    public Flux<ItemDto> getCart() {
        return itemService.findAllItemsByIds(cart.keySet().stream().toList())
                .map(this::convertItemWithCartCount);
    }

    @Override
    public Mono<Void> changeItemCountInCartByItemId(Long itemId, Action action) {
        switch (action) {
            case PLUS -> cart.compute(itemId, (k, v) -> isNull(v) ? 1 : v + 1);
            case MINUS -> cart.compute(itemId, (k, v) -> (isNull(v) || v == 0) ? 0 : v - 1);
            case DELETE -> cart.remove(itemId);
            default -> Mono.just(new NotFoundException("Действия: " + action + " не существует"));
        }
        return Mono.empty();
    }

    @Override
    public Flux<CartItemDto> getAndResetCart() {
        List<CartItemDto> cartItemDtos = cart.entrySet()
                .stream()
                .map(entry -> CartItemDto.builder()
                        .itemId(entry.getKey())
                        .count(entry.getValue())
                        .build())
                .toList();
        cart.clear();
        return Flux.fromIterable(cartItemDtos);
    }

    @Override
    public Mono<BigDecimal> getCartTotalSum() {
        return Flux.fromStream(cart.entrySet().stream())
                .flatMap(entry -> itemRepository.findById(entry.getKey())
                        .map(item -> item.getPrice().multiply(BigDecimal.valueOf(entry.getValue())))
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public Mono<BigDecimal> getBalance() {
        return oAuth2Service
                .getTokenValue()
                .flatMap(accessToken -> {
                    paymentApi.getApiClient().addDefaultHeader("Authorization", "Bearer " + accessToken);
                    return paymentApi.getBalance();
                })
                .map(BalanceResponse::getBalance)
                .onErrorResume(error -> {
                    log.error("Ошибка при обращении в платежный сервис: {}", error.getMessage(), error);
                    return Mono.just(BigDecimal.ONE.negate());
                });
    }

    private ItemDto convertItemWithCartCount(ItemDto item) {
        Integer cartCount = cart.getOrDefault(item.getId(), 0);
        item.setCount(cartCount);

        return item;
    }
}
