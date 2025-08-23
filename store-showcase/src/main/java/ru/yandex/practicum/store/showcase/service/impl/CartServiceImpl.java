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
import ru.yandex.practicum.store.showcase.repository.ItemRepository;
import ru.yandex.practicum.store.showcase.service.CartService;
import ru.yandex.practicum.store.showcase.service.ItemService;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.store.showcase.service.OAuth2Service;
import ru.yandex.practicum.store.showcase.service.SecurityService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.isNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final Map<Long, Map<Long, Integer>> cart = new ConcurrentHashMap<>();

    private final PaymentApi paymentApi;
    private final ItemService itemService;
    private final OAuth2Service oAuth2Service;
    private final ItemRepository itemRepository;
    private final SecurityService securityService;

    @Override
    public Flux<ItemDto> getCart() {
        return securityService.getCurrentUserId()
                .flatMapMany(userId -> {
                    Map<Long, Integer> userCart = cart.get(userId);

                    List<Long> ids = new ArrayList<>(userCart.keySet());
                    return itemService.findAllItemsByIds(ids)
                            .map(itemDto -> convertItemWithCartCount(itemDto, userCart));
                });
    }

    @Override
    public Mono<Void> changeItemCountInCartByItemId(Long itemId, Action action) {
        return securityService.getCurrentUserId()
                .flatMap(userId -> {
                    Map<Long, Integer> userCart = cart.computeIfAbsent(userId, k -> new HashMap<>());

                    switch (action) {
                        case PLUS -> userCart.compute(itemId, (k, v) -> isNull(v) ? 1 : v + 1);
                        case MINUS -> userCart.compute(itemId, (k, v) -> (isNull(v) || v == 0) ? 0 : v - 1);
                        case DELETE -> userCart.remove(itemId);
                        default -> log.info("not found action");
                    }
                    return Mono.empty();
                });
    }

    @Override
    public Flux<CartItemDto> getAndResetCart() {
        return securityService.getCurrentUserId()
                .flatMapMany(userId -> {
                    Map<Long, Integer> userCart = cart.get(userId);

                    Flux<CartItemDto> cartItems = Flux.fromStream(
                            userCart.entrySet().stream()
                                    .map(entry -> CartItemDto.builder()
                                            .itemId(entry.getKey())
                                            .count(entry.getValue())
                                            .build())
                    );
                    cart.remove(userId);
                    return cartItems;
                })
                .switchIfEmpty(Flux.empty());
    }

    @Override
    public Mono<BigDecimal> getCartTotalSum() {
        return securityService.getCurrentUserId()
                .flatMapMany(userId -> Flux.fromIterable((cart.get(userId).entrySet()))
                        .flatMap(userCart -> itemRepository.findById(userCart.getKey())
                                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(userCart.getValue())))
                        ))
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

    private ItemDto convertItemWithCartCount(ItemDto item, Map<Long, Integer> userCart) {
        item.setCount(userCart.computeIfAbsent(item.getId(), k -> 0));
        return item;
    }
}
