package ru.yandex.practicum.store.showcase.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("users")
@EqualsAndHashCode(of = "id")
public class UserEntity {

    @Id
    private Long id;

    private String username;

    private String password;
}
