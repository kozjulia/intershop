package ru.yandex.practicum.intershop.service;

import ru.yandex.practicum.intershop.dto.OrderDto;

import java.util.List;

public interface OrderService {

    Long createOrder();

    List<OrderDto> findOrders();

    OrderDto findOrderById(Long orderId);
}
