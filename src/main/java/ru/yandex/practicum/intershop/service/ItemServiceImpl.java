package ru.yandex.practicum.intershop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.intershop.dto.ItemDto;
import ru.yandex.practicum.intershop.dto.ItemSort;
import ru.yandex.practicum.intershop.mapper.ItemMapper;
import ru.yandex.practicum.intershop.model.ItemEntity;
import ru.yandex.practicum.intershop.repository.ItemRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemMapper itemMapper;
    private final ItemRepository itemRepository;

    public List<ItemDto> findAllPagingAndSorting(String search, ItemSort itemSort, Integer pageSize, Integer pageNumber) {

        Pageable page = resolvePageable(itemSort, pageSize, pageNumber);
        Page<ItemEntity> items = itemRepository.searchAllPagingAndSorting(search, page);

        return items.getContent()
                .stream()
                .map(itemMapper::toItemDto)
                .toList();
    }

    private Pageable resolvePageable(ItemSort itemSort, Integer pageSize, Integer pageNumber) {
        return switch (itemSort) {
            case NO -> PageRequest.of(pageNumber, pageSize);
            case ALPHA -> PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, "title"));
            case PRICE -> PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, "price"));
        };
    }
}
