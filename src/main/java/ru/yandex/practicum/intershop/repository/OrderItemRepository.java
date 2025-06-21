package ru.yandex.practicum.intershop.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.intershop.model.OrderItemEntity;
import ru.yandex.practicum.intershop.model.OrderItemKey;

@Repository
public interface OrderItemRepository extends R2dbcRepository<OrderItemEntity, OrderItemKey> {

}
