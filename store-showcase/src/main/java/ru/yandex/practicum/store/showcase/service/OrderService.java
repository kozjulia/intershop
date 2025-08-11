package ru.yandex.practicum.store.showcase.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.store.showcase.dto.OrderDto;

public interface OrderService {

    Mono<Long> createOrder();

    Flux<OrderDto> findOrders();

    Mono<OrderDto> findOrderById(Long orderId);
}
