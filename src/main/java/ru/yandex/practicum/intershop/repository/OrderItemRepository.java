package ru.yandex.practicum.intershop.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.model.ItemEntity;
import ru.yandex.practicum.intershop.model.OrderItemEntity;

import java.math.BigDecimal;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderItemRepository {

    private final DatabaseClient client;

    public Mono<Void> saveAll(List<OrderItemEntity> items) {
        return Flux.fromIterable(items)
                .flatMap(item ->
                        client.sql("INSERT INTO orders_items(order_id, item_id, count) VALUES(:orderId, :itemId, :count)")
                                .bind("orderId", item.getOrderId())
                                .bind("itemId", item.getItemId())
                                .bind("count", item.getCount())
                                .fetch()
                                .rowsUpdated())
                .then();
    }

    public Flux<ItemEntity> findByOrderId(Long orderId) {
        return client.sql("""
                        SELECT oi.item_id, items.title, items.description, items.img_path, oi.count, items.price
                        FROM orders_items AS oi 
                        JOIN items ON oi.item_id = items.id
                        WHERE oi.order_id = :orderId
                        """)
                .bind("orderId", orderId)
                .map((row, metadata) -> {
                    Long itemId = row.get("item_id", Long.class);
                    String title = row.get("title", String.class);
                    String description = row.get("description", String.class);
                    String imgPath = row.get("img_path", String.class);
                    Integer count = row.get("count", Integer.class);
                    BigDecimal price = row.get("price", BigDecimal.class);

                    return ItemEntity.builder()
                            .id(itemId)
                            .title(title)
                            .description(description)
                            .imgPath(imgPath)
                            .count(count)
                            .price(price)
                            .build();
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
