package ru.yandex.practicum.intershop.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

@Getter
@Setter
@Entity
@Builder
@Immutable
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders_items")
@EqualsAndHashCode(of = "id")
public class OrderItemEntity {

    @EmbeddedId
    private OrderItemKey id;

    Integer count;
}
