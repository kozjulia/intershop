package ru.yandex.practicum.intershop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;

import static java.util.function.UnaryOperator.identity;
import static java.util.stream.Collectors.toMap;

@Getter
@AllArgsConstructor
public enum Action {

    PLUS("plus", "Добавить один товар"),
    MINUS("minus", "Удалить один товар"),
    DELETE("delete", "Удалить товар из корзины)");

    private static final Map<String, Action> ACTION_BY_NAME = Arrays.stream(values())
            .collect(toMap(Action::getName, identity()));

    private final String name;
    private final String description;

    public static Action forName(String name) {
        return ACTION_BY_NAME.get(name);
    }
}
