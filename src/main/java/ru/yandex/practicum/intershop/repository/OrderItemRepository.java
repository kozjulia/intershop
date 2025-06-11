package ru.yandex.practicum.intershop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.intershop.model.OrderItemEntity;
import ru.yandex.practicum.intershop.model.OrderItemKey;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItemEntity, OrderItemKey> {

}
