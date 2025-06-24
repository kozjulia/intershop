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
@Builder
@Table("items")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Items {
    private Long id;
    private String title;
    private String description;
    private String imgPath;
    private Integer count;
    private java.math.BigDecimal price;
}
