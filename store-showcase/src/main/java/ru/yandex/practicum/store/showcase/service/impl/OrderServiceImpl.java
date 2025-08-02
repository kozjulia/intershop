package ru.yandex.practicum.store.showcase.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.store.showcase.dto.OrderDto;
import ru.yandex.practicum.store.showcase.mapper.ItemMapper;
import ru.yandex.practicum.store.showcase.model.OrderEntity;
import ru.yandex.practicum.store.showcase.model.OrderItemEntity;
import ru.yandex.practicum.store.showcase.repository.ItemRepository;
import ru.yandex.practicum.store.showcase.repository.OrderItemRepository;
import ru.yandex.practicum.store.showcase.repository.OrderRepository;
import ru.yandex.practicum.store.showcase.service.CartService;
import ru.yandex.practicum.store.showcase.service.ItemService;
import ru.yandex.practicum.store.showcase.service.OrderService;

@Service
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
    @CacheEvict(value = {"orders", "allOrders"}, allEntries = true)
    public Mono<Long> createOrder() {

        return cartService.getAndResetCart()
                .collectList()
                .flatMap(items ->
                        orderRepository.save(OrderEntity.builder().build())
                                .flatMap(order -> {
                                    Flux<OrderItemEntity> orderItemsFlux = Flux.fromIterable(items)
                                            .flatMap(item -> itemRepository.findById(item.getItemId())
                                                    .map(itemEntity -> OrderItemEntity.builder()
                                                            .orderId(order.getId())
                                                            .itemId(itemEntity.getId())
                                                            .count(item.getCount())
                                                            .build()));
                                    return orderItemsFlux.collectList()
                                            .flatMap(orderItemRepository::saveAll)
                                            .thenMany(Flux.fromIterable(items))
                                            .flatMap(itemService::updateItem)
                                            .then(Mono.just(order.getId()));
                                }));
    }


    @Override
    @Cacheable(value = "allOrders")
    public Flux<OrderDto> findOrders() {

        return orderRepository.findAll()
                .flatMap(order -> orderItemRepository.findByOrderId(order.getId())
                        .collectList()
                        .map(items -> OrderDto.builder()
                                .id(order.getId())
                                .items(itemMapper.toItemDtos(items))
                                .build())
                );
    }

    @Override
    @Cacheable(value = "orders", key = "#orderId", unless = "#result == null")
    public Mono<OrderDto> findOrderById(Long orderId) {

        return orderRepository.findById(orderId)
                .flatMap(order -> orderItemRepository.findByOrderId(order.getId())
                        .collectList()
                        .map(items -> OrderDto.builder()
                                .id(order.getId())
                                .items(itemMapper.toItemDtos(items))
                                .build())
                )
                .switchIfEmpty(Mono.just(new OrderDto()));
    }
}
