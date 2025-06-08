package ru.yandex.practicum.intershop.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemKey implements Serializable {

    private Long orderId;

    private Long itemId;
}
