package ru.yandex.practicum.intershop.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.model.OrderItemEntity;

@Repository
@RequiredArgsConstructor
public class OrderItemRepository {

    private final DatabaseClient client;

    public Mono<Void> saveAllAndFlush(Flux<OrderItemEntity> items) {
        return items.flatMap(item -> client.sql("INSERT INTO orders_items(order_id, item_id, count) VALUES(:orderId, :itemId, :count)")
                        .bind("orderId", item.getOrderId())
                        .bind("itemId", item.getItemId())
                        .bind("count", item.getCount())
                        .fetch()
                        .rowsUpdated())
                .then();
    }

    public Flux<OrderItemEntity> findByOrderId(Long orderId) {
        return client.sql("SELECT * FROM orders_items WHERE order_id = :orderId")
                .bind("orderId", orderId)
                .map((row, metadata) -> {
                    Long itemId = row.get("item_id", Long.class);
                    Integer count = row.get("count", Integer.class);
                    return new OrderItemEntity(
                            orderId,
                            itemId,
                            count
                    );
                })
                .all();
    }

    public Flux<OrderItemEntity> findAll() {
        return client.sql("SELECT order_id, item_id, count FROM orders_items")
                .map((row, metadata) -> {
                    Long orderId = row.get("order_id", Long.class);
                    Long itemId = row.get("item_id", Long.class);
                    Integer count = row.get("count", Integer.class);
                    return new OrderItemEntity(
                            orderId,
                            itemId,
                            count
                    );
                })
                .all();
    }
}
