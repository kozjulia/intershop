package ru.yandex.practicum.store.showcase.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.store.showcase.model.OrderEntity;

@Repository
public interface OrderRepository extends ReactiveCrudRepository<OrderEntity, Long> {

}
