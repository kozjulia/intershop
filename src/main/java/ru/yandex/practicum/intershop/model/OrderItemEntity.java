package ru.yandex.practicum.intershop.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
//@Entity
@Builder
//@Immutable
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders_items")
@EqualsAndHashCode(of = "id")
public class OrderItemEntity {

    //  @EmbeddedId
    private OrderItemKey id;

    Integer count;
}
