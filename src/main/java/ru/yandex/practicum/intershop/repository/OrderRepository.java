package ru.yandex.practicum.intershop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.intershop.model.OrderEntity;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

}
