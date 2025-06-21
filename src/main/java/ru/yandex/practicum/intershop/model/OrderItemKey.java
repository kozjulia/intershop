package ru.yandex.practicum.intershop.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
//@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemKey implements Serializable {

    //  @ManyToOne
    //   @JoinColumn(name = "order_id")
    private OrderEntity order;

    // @ManyToOne
    // @JoinColumn(name = "item_id")
    private ItemEntity item;
}
