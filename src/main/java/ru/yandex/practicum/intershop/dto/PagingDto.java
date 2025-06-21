package ru.yandex.practicum.intershop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PagingDto {

    private Integer pageNumber;
    private Integer pageSize;
    private Long totalSize;

    public Boolean hasNext() {
        return (long) (pageNumber - 1) * pageSize < totalSize;
    }

    public Boolean hasPrevious() {
        return (long) (pageNumber - 1) * pageSize > totalSize;
    }
}
