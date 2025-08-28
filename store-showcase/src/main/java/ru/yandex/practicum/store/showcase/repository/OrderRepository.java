package ru.yandex.practicum.store.showcase.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.store.showcase.model.OrderEntity;

@Repository
public interface OrderRepository extends ReactiveCrudRepository<OrderEntity, Long> {

    Flux<OrderEntity> findAllByUserId(Long userId);

    Mono<OrderEntity> findByIdAndUserId(Long id, Long userId);
}
