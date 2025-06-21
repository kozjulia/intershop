package ru.yandex.practicum.intershop.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Getter
@Setter
//@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "items")
@EqualsAndHashCode(of = "id")
public class ItemEntity {

    @Id
    // @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_seq")
    //  @SequenceGenerator(name = "item_seq", sequenceName = "SEQ_ITEM", allocationSize = 1)
    private Long id;

    private String title;

    private String description;

    private String imgPath;

    private Integer count;

    private BigDecimal price;
}
