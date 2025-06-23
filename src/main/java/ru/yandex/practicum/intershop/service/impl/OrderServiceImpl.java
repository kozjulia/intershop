package ru.yandex.practicum.intershop.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.dto.ItemDto;
import ru.yandex.practicum.intershop.dto.OrderDto;
import ru.yandex.practicum.intershop.mapper.ItemMapper;
import ru.yandex.practicum.intershop.model.ItemEntity;
import ru.yandex.practicum.intershop.model.OrderEntity;
import ru.yandex.practicum.intershop.model.OrderItemEntity;
import ru.yandex.practicum.intershop.model.SequenceGenerator;
import ru.yandex.practicum.intershop.repository.ItemRepository;
import ru.yandex.practicum.intershop.repository.OrderItemRepository;
import ru.yandex.practicum.intershop.repository.OrderRepository;
import ru.yandex.practicum.intershop.service.CartService;
import ru.yandex.practicum.intershop.service.ItemService;
import ru.yandex.practicum.intershop.service.OrderService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final ItemMapper itemMapper;
    private final CartService cartService;
    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;
    private final SequenceGenerator sequenceGenerator;
    private final OrderItemRepository orderItemRepository;

    @Override
    @Transactional
    public Mono<Long> createOrder() {

        return cartService.getAndResetCart()
                .collectList()
                .filter(items -> !items.isEmpty())
                .flatMap(items ->
                        sequenceGenerator.generateOrderId()
                                .flatMap(id -> {
                                    OrderEntity order = new OrderEntity();
                                    order.setId(id);
                                    return orderRepository.save(order);
                                })
                                .flatMap(orderEntity -> {
                                    Long orderId = orderEntity.getId();

                                    Flux<OrderItemEntity> orderItems = Flux.fromIterable(items)
                                            .map(item -> OrderItemEntity.builder()
                                                    .orderId(orderId)
                                                    .itemId(item.getItemId())
                                                    .count(item.getCount())
                                                    .build());

                                    return orderItemRepository.saveAllAndFlush(orderItems)
                                            .thenMany(Flux.fromIterable(items))
                                            .flatMap(itemService::updateItem)
                                            .then(Mono.just(orderId));
                                }));
    }


    @Override
    public Flux<OrderDto> findOrders() {

        return orderRepository.findAll()
                .flatMap(order -> orderItemRepository.findByOrderId(order.getId())
                        .collectList()
                        .flatMap(orderItems -> {

                            Map<Long, Integer> itemCountMap = orderItems.stream()
                                    .collect(Collectors.toMap(
                                            OrderItemEntity::getItemId,
                                            OrderItemEntity::getCount
                                    ));

                            List<Long> itemIds = orderItems.stream()
                                    .map(OrderItemEntity::getItemId)
                                    .toList();

                            if (itemIds.isEmpty()) {
                                return Mono.just(OrderDto.builder()
                                        .id(order.getId())
                                        .items(Collections.emptyList())
                                        .build());
                            }

                            return itemRepository.findAllByIdIn(itemIds)
                                    .map(itemMapper::toItemDto)
                                    .map(dto -> getUpdatedItem(dto, itemCountMap))
                                    .collectList()
                                    .map(itemDtos -> OrderDto.builder()
                                            .id(order.getId())
                                            .items(itemDtos)
                                            .build());
                        }));
    }

    @Override
    public Mono<OrderDto> findOrderById(Long orderId) {

        return orderRepository.findById(orderId)
                .switchIfEmpty(Mono.just(OrderEntity.builder().build()))
                .flatMap(order -> orderItemRepository.findByOrderId(orderId)
                        .collectList()
                        .flatMap(orderItems -> {

                            List<Long> itemIds = orderItems.stream()
                                    .map(OrderItemEntity::getItemId)
                                    .toList();

                            if (itemIds.isEmpty()) {
                                return Mono.just(OrderDto.builder()
                                        .id(orderId)
                                        .items(Collections.emptyList())
                                        .build());
                            }

                            return convertToItemDtos(itemRepository.findAllByIdIn(itemIds), orderId, orderItems)
                                    .collectList()
                                    .map(itemDtos -> OrderDto.builder()
                                            .id(orderId)
                                            .items(itemDtos)
                                            .build());
                        }));
    }

    private Flux<ItemDto> convertToItemDtos(Flux<ItemEntity> items, Long orderId, List<OrderItemEntity> orderItems) {

        Map<Long, Integer> itemCountMap = orderItems.stream()
                .filter(oi -> oi.getOrderId().equals(orderId))
                .collect(Collectors.toMap(
                        OrderItemEntity::getItemId,
                        OrderItemEntity::getCount
                ));

        return items
                .map(itemEntity -> {
                    ItemDto dto = itemMapper.toItemDto(itemEntity);
                    return getUpdatedItem(dto, itemCountMap);
                });
    }

    private ItemDto getUpdatedItem(ItemDto itemDto, Map<Long, Integer> itemCountMap) {//List<OrderItemEntity> orderItems) {

        Integer count = itemCountMap.getOrDefault(itemDto.getId(), 0);
        return ItemDto.builder()
                .id(itemDto.getId())
                .title(itemDto.getTitle())
                .price(itemDto.getPrice())
                .count(count)
                .build();
    }
}
