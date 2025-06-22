package ru.yandex.practicum.intershop.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
@EqualsAndHashCode(of = "id")
public class OrderEntity {

    @Id
    private Long id;

    private List<Long> itemIds;
}
