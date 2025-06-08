package ru.yandex.practicum.intershop.service;

import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.intershop.dto.CartItemDto;
import ru.yandex.practicum.intershop.dto.ItemDto;
import ru.yandex.practicum.intershop.dto.ItemSort;

import java.math.BigDecimal;
import java.util.List;

public interface ItemService {

    ItemDto getItemById(Long itemId);

    byte[] getItemImageByImagePath(String imagePath);

    List<ItemDto> findAllItemsPagingAndSorting(String search, ItemSort itemSort, Integer pageSize, Integer pageNumber);

    List<ItemDto> findAllItemsByIds(List<Long> itemIds);

    Long addItem(String title, String description, MultipartFile image, Integer count, BigDecimal price);

    void editItem(Long itemId, String title, String description, MultipartFile image, Integer count, BigDecimal price);

    void deleteItem(Long itemId);

    void updateItem(CartItemDto cartItemDto);
}
