package ru.yandex.practicum.intershop.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.yandex.practicum.intershop.dto.CartItemDto;
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
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private static final String IMAGE_UPLOAD_ERROR_TEMPLATE = "Фото для товара с id: {} не получилось сохранить";

    private final ItemMapper itemMapper;
    private final ItemRepository itemRepository;

    @Value("${intershop.path-for-upload-image}")
    private String pathForUploadImage;

    @Override
    public Mono<ItemDto> getItemById(Long itemId) {

        return itemRepository.findById(itemId)
                .map(itemMapper::toItemDto)
                .switchIfEmpty(Mono.empty());
    }

    @Override
    public Mono<byte[]> getItemImageByImagePath(String imagePath) {

        return Mono.fromCallable(() -> {
                    Path path = Path.of(pathForUploadImage).resolve(imagePath);
                    return Files.readAllBytes(path);
                })
                .subscribeOn(Schedulers.boundedElastic()) // Выполняем IO в пуле для блокирующих задач
                .onErrorResume(IOException.class, e -> {
                    log.error("Фото для товара с путем: {} не получилось загрузить", imagePath, e);
                    return Mono.empty();
                });
    }

    @Override
    public Flux<ItemDto> findAllItemsPagingAndSorting(String search, ItemSort itemSort, Integer pageSize, Integer pageNumber) {

        int offset = Math.max(0, (pageNumber - 1) * pageSize);
        String sortColumn = resolveSortColumn(itemSort);

        return itemRepository
                .searchAllPagingAndSorting(search, sortColumn, pageSize, offset)
                .map(itemMapper::toItemDto);
    }

    @Override
    public Flux<ItemDto> findAllItemsByIds(List<Long> itemIds) {

        return itemRepository.findAllByIdIn(itemIds)
                .map(itemMapper::toItemDto);
    }

    @Override
    public Mono<Long> addItem(String title, String description, MultipartFile image, Integer count, BigDecimal price) {

        ItemEntity item = ItemEntity.builder()
                .title(title)
                .description(description)
                .count(count)
                .price(price)
                .build();

        return itemRepository.save(item)
                .flatMap(savedItem -> {
                    Long itemId = savedItem.getId();

                    if (image.isEmpty()) {
                        return Mono.just(itemId);
                    }

                    return uploadImage(itemId, image)
                            .flatMap(imgPath -> itemRepository.updateImagePath(itemId, imgPath)
                                    .thenReturn(itemId))
                            .onErrorResume(IOException.class, ex -> {
                                log.warn("Не удалось загрузить изображение для товара с ID {}", itemId, ex);
                                return Mono.just(itemId);
                            });
                });
    }

    @Override
    public Mono<Void> editItem(Long itemId, String title, String description, MultipartFile image, Integer count, BigDecimal price) {

        return itemRepository.findById(itemId)
                .switchIfEmpty(Mono.error(new NotFoundException("Товара с id: " + itemId + " не существует")))
                .flatMap(existingItem -> {
                    String currentImagePath = existingItem.getImgPath();

                    Mono<String> imagePathMono;

                    if (image.isEmpty()) {
                        imagePathMono = Mono.just(currentImagePath);
                    } else {
                        imagePathMono = uploadImage(itemId, image)
                                .onErrorResume(IOException.class, ex -> {
                                    log.warn(IMAGE_UPLOAD_ERROR_TEMPLATE, itemId, ex);
                                    return Mono.just(currentImagePath);
                                });
                    }

                    return imagePathMono
                            .flatMap(imgPath -> {
                                ItemEntity updatedItem = ItemEntity.builder()
                                        .id(itemId)
                                        .title(title)
                                        .description(description)
                                        .imgPath(imgPath)
                                        .count(count)
                                        .price(price)
                                        .build();

                                return itemRepository.save(updatedItem).thenReturn(imgPath);
                            })
                            .then();
                });
    }

    @Override
    public Mono<Void> deleteItem(Long itemId) {

        return itemRepository.deleteById(itemId);
    }

    @Override
    public Mono<Void> updateItem(CartItemDto cartItemDto) {

        return itemRepository.updateCountItem(cartItemDto.getItemId(), cartItemDto.getCount());
    }

    private String resolveSortColumn(ItemSort itemSort) {
        return switch (itemSort) {
            case NO -> "id";
            case ALPHA -> "title";
            case PRICE -> "price";
        };
    }

    private Mono<String> uploadImage(Long itemId, MultipartFile image) {

        return Mono.fromCallable(() -> {
                    String imagePath = "item-image-" + itemId + "." + getExtension(image.getOriginalFilename());

                    Path uploadDir = Paths.get(System.getProperty("user.dir"), pathForUploadImage);
                    Files.createDirectories(uploadDir);

                    Path path = uploadDir.resolve(imagePath);
                    image.transferTo(path);

                    return imagePath;
                })
                .subscribeOn(Schedulers.boundedElastic()) // Выполняем в неблокирующем пуле
                .onErrorMap(IOException.class, exc -> {
                    log.error(IMAGE_UPLOAD_ERROR_TEMPLATE, itemId, exc);
                    return new IOException("Не удалось сохранить изображение для товара с id: " + itemId);
                });
    }
}
