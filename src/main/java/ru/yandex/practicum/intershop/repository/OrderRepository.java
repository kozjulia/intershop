package ru.yandex.practicum.intershop.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.intershop.model.OrderEntity;

@Repository
public interface OrderRepository extends R2dbcRepository<OrderEntity, Long> {

}
