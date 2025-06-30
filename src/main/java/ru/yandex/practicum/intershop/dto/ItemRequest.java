package ru.yandex.practicum.intershop.dto;

import org.springframework.http.codec.multipart.FilePart;

import java.math.BigDecimal;

public record ItemRequest(

        String title,
        String description,
        FilePart image,
        Integer count,
        BigDecimal price
) {

}
