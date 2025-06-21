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
//@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
@EqualsAndHashCode(of = "id")
public class OrderEntity {

    @Id
    //   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_seq")
    //   @SequenceGenerator(name = "order_seq", sequenceName = "SEQ_ORDER", allocationSize = 1)
    private Long id;

    /*  @ManyToMany
      @JoinTable(
              name = "orders_items",
              joinColumns = @JoinColumn(name = "order_id"),
              inverseJoinColumns = @JoinColumn(name = "item_id"))*/
    private List<ItemEntity> items;
}
