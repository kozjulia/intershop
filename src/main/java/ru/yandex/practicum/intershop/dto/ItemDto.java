package ru.yandex.practicum.intershop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {

    private Long id;
    private String title;
    private String description;
    private String imgPath;
    private Integer count;
    private BigDecimal price;
}
