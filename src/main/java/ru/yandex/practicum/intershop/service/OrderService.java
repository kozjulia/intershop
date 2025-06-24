package ru.yandex.practicum.intershop.service;

import ru.yandex.practicum.intershop.dto.OrderDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderService {

    Mono<Long> createOrder();

    Flux<OrderDto> findOrders();

    Mono<OrderDto> findOrderById(Long orderId);
}
