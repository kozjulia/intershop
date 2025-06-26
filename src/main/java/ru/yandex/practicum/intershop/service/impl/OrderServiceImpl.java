package ru.yandex.practicum.intershop.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.dto.OrderDto;
import ru.yandex.practicum.intershop.mapper.ItemMapper;
import ru.yandex.practicum.intershop.model.OrderEntity;
import ru.yandex.practicum.intershop.model.OrderItemEntity;
import ru.yandex.practicum.intershop.repository.ItemRepository;
import ru.yandex.practicum.intershop.repository.OrderItemRepository;
import ru.yandex.practicum.intershop.repository.OrderRepository;
import ru.yandex.practicum.intershop.service.CartService;
import ru.yandex.practicum.intershop.service.ItemService;
import ru.yandex.practicum.intershop.service.OrderService;

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
