package ru.yandex.practicum.store.showcase.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ItemSort {

    NO("Без сортировки"),
    ALPHA("По алфавиту"),
    PRICE("По цене");

    private final String description;
}
