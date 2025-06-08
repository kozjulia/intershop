package ru.yandex.practicum.intershop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {

    private Long itemId;
    private Integer count;
}
