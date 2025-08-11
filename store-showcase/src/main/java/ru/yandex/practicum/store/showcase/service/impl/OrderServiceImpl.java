package ru.yandex.practicum.store.showcase.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.store.showcase.client.api.PaymentApi;
import ru.yandex.practicum.store.showcase.client.model.PaymentRequest;
import ru.yandex.practicum.store.showcase.dto.OrderDto;
import ru.yandex.practicum.store.showcase.exception.PaymentException;
import ru.yandex.practicum.store.showcase.mapper.ItemMapper;
import ru.yandex.practicum.store.showcase.model.OrderEntity;
import ru.yandex.practicum.store.showcase.model.OrderItemEntity;
import ru.yandex.practicum.store.showcase.repository.ItemRepository;
import ru.yandex.practicum.store.showcase.repository.OrderItemRepository;
import ru.yandex.practicum.store.showcase.repository.OrderRepository;
import ru.yandex.practicum.store.showcase.service.CartService;
import ru.yandex.practicum.store.showcase.service.ItemService;
import ru.yandex.practicum.store.showcase.service.OrderService;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final String PAYMENT_ERROR = "Ошибка при обращении в платежный сервис";

    private final ItemMapper itemMapper;
    private final PaymentApi paymentApi;
    private final CartService cartService;
    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    @Transactional
    @CacheEvict(value = {"orders", "allOrders"}, allEntries = true)
    public Mono<Long> createOrder() {

        return cartService.getCartTotalSum()
                .flatMap(totalSum ->
                        paymentApi.makePayment(new PaymentRequest().sum(totalSum))
                                .onErrorResume(error -> {
                                    log.error(PAYMENT_ERROR + ": {}", error.getMessage(), error);
                                    return Mono.error(new PaymentException(PAYMENT_ERROR, error));
                                })
                                .thenMany(cartService.getAndResetCart())
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
                                                                            .build())
                                                            );
                                                    return orderItemsFlux.collectList()
                                                            .flatMap(orderItemRepository::saveAll)
                                                            .thenMany(Flux.fromIterable(items))
                                                            .flatMap(itemService::updateItem)
                                                            .then(Mono.just(order.getId()));
                                                })
                                )
                );
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
