package ru.yandex.practicum.intershop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.service.ItemService;

@Controller
@RequiredArgsConstructor
public class ImageController {

    private final ItemService itemService;

    /**
     * Получение изображения товара
     *
     * @param imagePath Путь к изображению товара
     * @return Набор байт картинки товара
     */
    @GetMapping("/{imagePath}")
    public Mono<ResponseEntity<byte[]>> getImageItem(@PathVariable String imagePath) {
        return itemService.getItemImageByImagePath(imagePath)
                .map(bytes -> ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(bytes));
    }
}
