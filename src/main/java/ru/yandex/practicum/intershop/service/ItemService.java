package ru.yandex.practicum.intershop.service;

import ru.yandex.practicum.intershop.dto.ItemDto;
import ru.yandex.practicum.intershop.dto.ItemSort;

import java.util.List;

public interface ItemService {

    List<ItemDto> findAllPagingAndSorting(String search, ItemSort itemSort, Integer pageSize, Integer pageNumber);
}
