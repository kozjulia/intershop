package ru.yandex.practicum.intershop.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.intershop.dto.CartItemDto;
import ru.yandex.practicum.intershop.dto.ItemDto;
import ru.yandex.practicum.intershop.dto.OrderDto;
import ru.yandex.practicum.intershop.mapper.ItemMapper;
import ru.yandex.practicum.intershop.model.ItemEntity;
import ru.yandex.practicum.intershop.model.OrderEntity;
import ru.yandex.practicum.intershop.model.OrderItemEntity;
import ru.yandex.practicum.intershop.model.OrderItemKey;
import ru.yandex.practicum.intershop.repository.ItemRepository;
import ru.yandex.practicum.intershop.repository.OrderItemRepository;
import ru.yandex.practicum.intershop.repository.OrderRepository;
import ru.yandex.practicum.intershop.service.CartService;
import ru.yandex.practicum.intershop.service.ItemService;
import ru.yandex.practicum.intershop.service.OrderService;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final ItemMapper itemMapper;
    private final CartService cartService;
    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    @Transactional
    public Long createOrder() {

        List<CartItemDto> items = cartService.getAndResetCart();

        Long orderId = orderRepository.saveAndFlush(new OrderEntity())
                .getId();

        List<OrderItemEntity> orderItemEntities = items.stream()
                .map(item -> OrderItemEntity.builder()
                        .id(OrderItemKey.builder()
                                .order(orderRepository.getReferenceById(orderId))
                                .item(itemRepository.getReferenceById(item.getItemId()))
                                .build())
                        .count(item.getCount())
                        .build())
                .toList();

        orderItemRepository.saveAllAndFlush(orderItemEntities);

        items.forEach(itemService::updateItem);

        return orderId;
    }

    @Override
    public List<OrderDto> findOrders() {

        List<OrderItemEntity> orderItems = orderItemRepository.findAll();

        List<OrderEntity> orders = orderRepository.findAll();
        return orders.stream()
                .map(order -> OrderDto.builder()
                        .id(order.getId())
                        .items(convertToItemDtos(order.getItems(), order.getId(), orderItems))
                        .build())
                .toList();
    }

    @Override
    public OrderDto findOrderById(Long orderId) {

        List<OrderItemEntity> orderItems = orderItemRepository.findAll()
                .stream()
                .filter(orderItem -> orderItem.getId().getOrder().getId().equals(orderId))
                .toList();

        return orderRepository.findById(orderId)
                .map(order -> OrderDto.builder()
                        .id(orderId)
                        .items(convertToItemDtos(order.getItems(), orderId, orderItems))
                        .build())
                .orElse(new OrderDto());
    }

    private List<ItemDto> convertToItemDtos(List<ItemEntity> items, Long orderId, List<OrderItemEntity> orderItems) {
        List<ItemDto> itemDtos = itemMapper.toItemDtos(items);
        return itemDtos
                .stream()
                .map(itemDto -> getUpdatedItem(itemDto, orderId, orderItems))
                .toList();
    }

    private ItemDto getUpdatedItem(ItemDto itemDto, Long orderId, List<OrderItemEntity> orderItems) {
        Integer orderCount = orderItems.stream()
                .filter(orderItem -> orderItem.getId().getOrder().getId().equals(orderId))
                .filter(orderItem -> orderItem.getId().getItem().getId().equals(itemDto.getId()))
                .map(OrderItemEntity::getCount)
                .findFirst()
                .orElse(0);

        itemDto.setCount(orderCount);
        return itemDto;
    }
}
