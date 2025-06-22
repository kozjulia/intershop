package ru.yandex.practicum.intershop.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Immutable;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Builder
@Immutable
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders_items")
@EqualsAndHashCode(of = {"orderId", "itemId"})
public class OrderItemEntity {

    private Long orderId;
    private Long itemId;
    private Integer count;
}
