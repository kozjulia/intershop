package ru.yandex.practicum.intershop.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.intershop.dto.ItemDto;
import ru.yandex.practicum.intershop.dto.ItemSort;
import ru.yandex.practicum.intershop.exception.NotFoundException;
import ru.yandex.practicum.intershop.mapper.ItemMapper;
import ru.yandex.practicum.intershop.model.ItemEntity;
import ru.yandex.practicum.intershop.repository.ItemRepository;
import ru.yandex.practicum.intershop.service.ItemService;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.apache.commons.io.FilenameUtils.getExtension;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private static final String IMAGE_UPLOAD_ERROR_TEMPLATE = "Фото для товара с id: {} не получилось сохранить";

    private final ItemMapper itemMapper;
    private final ItemRepository itemRepository;

    @Value("${intershop.path-for-upload-image}")
    private String pathForUploadImage;

    @Override
    public ItemDto getItemById(Long itemId) {

        return itemRepository.findById(itemId)
                .map(itemMapper::toItemDto)
                .orElse(null);
    }

    @Override
    public byte[] getItemImageByImagePath(String imagePath) {

        try {
            Path path = Path.of(pathForUploadImage).resolve(imagePath);

            return Files.readAllBytes(path);
        } catch (IOException e) {
            log.error("Фото для товара с путем: {} не получилось загрузить", imagePath);
        }

        return new byte[0];
    }

    @Override
    public List<ItemDto> findAllItemsPagingAndSorting(String search, ItemSort itemSort, Integer pageSize, Integer pageNumber) {

        Pageable page = resolvePageable(itemSort, pageSize, pageNumber - 1);
        Page<ItemEntity> items = itemRepository.searchAllPagingAndSorting(search, page);

        return items.getContent()
                .stream()
                .map(itemMapper::toItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> findAllItemsByIds(List<Long> itemIds) {

        return itemRepository.findAllByIdIn(itemIds)
                .stream()
                .map(itemMapper::toItemDto)
                .toList();
    }

    @Override
    @Transactional
    public Long addItem(String title, String description, MultipartFile image, Integer count, BigDecimal price) {

        ItemEntity item = ItemEntity.builder()
                .title(title)
                .description(description)
                .count(count)
                .price(price)
                .build();

        itemRepository.save(item);
        Long itemId = item.getId();

        if (image.isEmpty()) {
            return itemId;
        }
        try {
            String imgPath = uploadImage(itemId, image);

            itemRepository.updateImagePath(itemId, imgPath);
        } catch (IOException e) {
            log.error(IMAGE_UPLOAD_ERROR_TEMPLATE, itemId);
        }

        return itemId;
    }

    @Override
    @Transactional
    public void editItem(Long itemId, String title, String description, MultipartFile image, Integer count, BigDecimal price) {

        ItemEntity item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Товара с id: " + itemId + " не существует"));
        String imagePath = item.getImgPath();

        if (!image.isEmpty()) {
            try {
                imagePath = uploadImage(itemId, image);
            } catch (IOException e) {
                log.error(IMAGE_UPLOAD_ERROR_TEMPLATE, itemId);
            }
        }

        item.setTitle(title);
        item.setDescription(description);
        item.setImgPath(imagePath);
        item.setCount(count);
        item.setPrice(price);
        itemRepository.save(item);
    }

    @Override
    @Transactional
    public void deleteItem(Long itemId) {

        itemRepository.deleteById(itemId);
    }

    private Pageable resolvePageable(ItemSort itemSort, Integer pageSize, Integer pageNumber) {
        return switch (itemSort) {
            case NO -> PageRequest.of(pageNumber, pageSize);
            case ALPHA -> PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, "title"));
            case PRICE -> PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, "price"));
        };
    }

    private String uploadImage(Long itemId, MultipartFile image) throws IOException {

        String imagePath = "item-image-" + itemId + "." + getExtension(image.getOriginalFilename());

        Path uploadDir = Paths.get(System.getProperty("user.dir"), pathForUploadImage);
        Files.createDirectories(uploadDir);

        Path path = uploadDir.resolve(imagePath);
        image.transferTo(path);

        return imagePath;
    }
}
