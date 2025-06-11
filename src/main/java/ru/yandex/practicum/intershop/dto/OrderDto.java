package ru.yandex.practicum.intershop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    private Long id;
    private List<ItemDto> items;
    private BigDecimal totalSum;

    public BigDecimal getTotalSum() {
        return items.stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getCount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
