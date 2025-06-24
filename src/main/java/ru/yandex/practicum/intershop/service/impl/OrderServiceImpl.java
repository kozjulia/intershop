package ru.yandex.practicum.intershop.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.intershop.dto.ItemDto;
import ru.yandex.practicum.intershop.dto.OrderDto;
import ru.yandex.practicum.intershop.mapper.ItemMapper;
import ru.yandex.practicum.intershop.model.Items;
import ru.yandex.practicum.intershop.model.Orders;
import ru.yandex.practicum.intershop.model.OrderItemEntity;
import ru.yandex.practicum.intershop.model.OrderItemKey;
import ru.yandex.practicum.intershop.repository.ItemRepository;
import ru.yandex.practicum.intershop.repository.OrderItemRepository;
import ru.yandex.practicum.intershop.repository.OrderRepository;
import ru.yandex.practicum.intershop.service.CartService;
import ru.yandex.practicum.intershop.service.ItemService;
import ru.yandex.practicum.intershop.service.OrderService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    public Mono<Long> createOrder() {
        return cartService.getAndResetCart().collectList()
                .flatMap(items -> orderRepository.save(Orders.builder().build())
                        .flatMap(order -> {
                            Flux<OrderItemEntity> orderItemsFlux = Flux.fromIterable(items)
                                    .flatMap(item -> itemRepository.findById(item.getItemId())
                                            .map(itemEntity -> OrderItemEntity.builder()
                                                    .id(OrderItemKey.builder()
                                                            .order(order)
                                                            .item(itemEntity)
                                                            .build())
                                                    .count(item.getCount())
                                                    .build()));
                            return orderItemsFlux.collectList()
                                    .flatMap(orderItemEntities -> orderItemRepository.saveAll(orderItemEntities).collectList())
                                    .thenMany(Flux.fromIterable(items))
                                    .flatMap(itemService::updateItem)
                                    .then(Mono.just(order.getId()));
                        }));
    }

    @Override
    public Flux<OrderDto> findOrders() {
        return orderRepository.findAll()
                .flatMap(order -> orderItemRepository.findAll()
                        .filter(orderItem -> orderItem.getId().getOrder().getId().equals(order.getId()))
                        .collectList()
                        .flatMap(orderItems -> {
                            List<Items> items = order.getItems();
                            List<ItemDto> itemDtos = itemMapper.toItemDtos(items);
                            List<ItemDto> updatedItemDtos = itemDtos.stream()
                                    .map(itemDto -> getUpdatedItem(itemDto, order.getId(), orderItems))
                                    .toList();
                            return Mono.just(OrderDto.builder()
                                    .id(order.getId())
                                    .items(updatedItemDtos)
                                    .build());
                        }));
    }

    @Override
    public Mono<OrderDto> findOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .flatMap(order -> orderItemRepository.findAll()
                        .filter(orderItem -> orderItem.getId().getOrder().getId().equals(orderId))
                        .collectList()
                        .flatMap(orderItems -> {
                            List<Items> items = order.getItems();
                            List<ItemDto> itemDtos = itemMapper.toItemDtos(items);
                            List<ItemDto> updatedItemDtos = itemDtos.stream()
                                    .map(itemDto -> getUpdatedItem(itemDto, orderId, orderItems))
                                    .toList();
                            return Mono.just(OrderDto.builder()
                                    .id(orderId)
                                    .items(updatedItemDtos)
                                    .build());
                        })
                ).switchIfEmpty(Mono.just(new OrderDto()));
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
