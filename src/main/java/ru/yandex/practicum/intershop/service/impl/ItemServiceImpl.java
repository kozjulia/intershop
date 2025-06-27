package ru.yandex.practicum.intershop.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.dto.CartItemDto;
import ru.yandex.practicum.intershop.dto.ItemDto;
import ru.yandex.practicum.intershop.dto.ItemSort;
import ru.yandex.practicum.intershop.mapper.ItemMapper;
import ru.yandex.practicum.intershop.model.ItemEntity;
import ru.yandex.practicum.intershop.repository.ItemRepository;
import ru.yandex.practicum.intershop.service.ItemService;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemMapper itemMapper;
    private final ItemRepository itemRepository;

    @Value("${intershop.path-for-upload-image}")
    private String pathForUploadImage;

    @Override
    public Mono<ItemDto> getItemById(Long itemId) {

        return itemRepository.findById(itemId)
                .map(itemMapper::toItemDto);
    }

    @Override
    public Mono<byte[]> getItemImageByImagePath(String imagePath) {
        Path path = Path.of(pathForUploadImage).resolve(imagePath);
        if (Files.exists(path)) {
            return Mono.fromCallable(() -> Files.readAllBytes(path));
        }
        return Mono.empty();
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

        return itemRepository.findAllById(itemIds)
                .map(itemMapper::toItemDto);
    }

    @Override
    @Transactional
    public Mono<Long> addItem(String title, String description, FilePart image, Integer count, BigDecimal price) {

        ItemEntity item = ItemEntity.builder()
                .title(title)
                .description(description)
                .count(count)
                .price(price)
                .build();

        return itemRepository.save(item)
                .flatMap(saved -> {
                    if (image == null) {
                        return Mono.just(saved.getId());
                    }
                    String imgPath = "item-image-" + saved.getId() + "." + getExtension(image.filename());
                    Path uploadDir = Paths.get(System.getProperty("user.dir"), pathForUploadImage);
                    Path filePath = uploadDir.resolve(imgPath);
                    return Mono.fromCallable(() -> Files.createDirectories(uploadDir))
                            .then(image.transferTo(filePath))
                            .then(itemRepository.findById(saved.getId())
                                    .flatMap(entity -> {
                                        entity.setImgPath(imgPath);
                                        return itemRepository.save(entity);
                                    })
                                    .thenReturn(saved.getId()));
                });
    }

    @Override
    @Transactional
    public Mono<Void> editItem(Long itemId, String title, String description, FilePart image, Integer count, BigDecimal price) {

        return itemRepository.findById(itemId)
                .flatMap(item -> {
                    Mono<String> imagePathMono;
                    if (image != null) {
                        String imgPath = "item-image-" + itemId + "." + getExtension(image.filename());
                        Path uploadDir = Paths.get(System.getProperty("user.dir"), pathForUploadImage);
                        Path filePath = uploadDir.resolve(imgPath);
                        imagePathMono = Mono.fromCallable(() -> Files.createDirectories(uploadDir))
                                .then(image.transferTo(filePath))
                                .thenReturn(imgPath);
                    } else {
                        imagePathMono = Mono.just(item.getImgPath());
                    }
                    return imagePathMono.flatMap(imgPath -> {
                        item.setTitle(title);
                        item.setDescription(description);
                        item.setImgPath(imgPath);
                        item.setCount(count);
                        item.setPrice(price);
                        return itemRepository.save(item).then();
                    });
                });
    }

    @Override
    @Transactional
    public Mono<Void> deleteItem(Long itemId) {

        return itemRepository.deleteById(itemId);
    }

    @Override
    @Transactional
    public Mono<Void> updateItem(CartItemDto cartItemDto) {
        return itemRepository.findById(cartItemDto.getItemId())
                .flatMap(item -> {
                    item.setCount(item.getCount() - cartItemDto.getCount());
                    return itemRepository.save(item).then();
                });
    }

    private static String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? "" : filename.substring(dotIndex + 1);
    }

    private String resolveSortColumn(ItemSort itemSort) {
        return switch (itemSort) {
            case NO -> "id";
            case ALPHA -> "title";
            case PRICE -> "price";
        };
    }
}
