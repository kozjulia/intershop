package ru.yandex.practicum.intershop.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.dto.OrderDto;

public interface OrderService {

    Mono<Long> createOrder();

    Flux<OrderDto> findOrders();

    Mono<OrderDto> findOrderById(Long orderId);
}
