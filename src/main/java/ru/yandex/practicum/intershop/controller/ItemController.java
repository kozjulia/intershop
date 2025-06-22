package ru.yandex.practicum.intershop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.dto.Action;
import ru.yandex.practicum.intershop.service.CartService;
import ru.yandex.practicum.intershop.service.ItemService;

import java.math.BigDecimal;

import static ru.yandex.practicum.intershop.configuration.constants.TemplateConstants.REDIRECT_ITEMS;
import static ru.yandex.practicum.intershop.configuration.constants.TemplateConstants.REDIRECT_MAIN_ITEMS;
import static ru.yandex.practicum.intershop.configuration.constants.TemplateConstants.TEMPLATE_ADD_ITEM;
import static ru.yandex.practicum.intershop.configuration.constants.TemplateConstants.TEMPLATE_ITEM;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final CartService cartService;
    private final ItemService itemService;

    /**
     * Карточка товара
     *
     * @param itemId Идентификатор товара
     * @param model  Модель
     * @return Шаблон "item.html"
     */
    @GetMapping("/{id}")
    public Mono<String> getItemById(
            @PathVariable("id") Long itemId,
            Model model) {

        return itemService.getItemById(itemId)
                .doOnNext(item -> model.addAttribute("item", item))
                .thenReturn(TEMPLATE_ITEM);
    }

    /**
     * Страница добавления товара
     *
     * @param model Модель
     * @return Шаблон "add-item.html"
     */
    @GetMapping("/add")
    public Mono<String> getAddingForm(Model model) {
        model.addAttribute("item", null);

        return Mono.just(TEMPLATE_ADD_ITEM);
    }

    /**
     * Добавление товара
     *
     * @param title       Название товара
     * @param description Описание товара
     * @param image       Файл картинки товара
     * @param count       Количество товара
     * @param price       Цена товара
     * @return Редирект на созданный "/items/{id}"
     */
    @PostMapping
    public Mono<String> addItem(
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestPart MultipartFile image,
            @RequestParam(required = false, defaultValue = "0") Integer count,
            @RequestParam(required = false, defaultValue = "0,00") BigDecimal price
    ) {

        return itemService.addItem(title, description, image, count, price)
                .map(itemId -> REDIRECT_ITEMS + itemId);
    }

    /**
     * Страница редактирования товара
     *
     * @param itemId Идентификатор товара
     * @param model  Модель
     * @return Редирект на форму редактирования товара "add-item.html"
     */
    @GetMapping("/{id}/edit")
    public Mono<String> getEditingForm(
            @PathVariable("id") Long itemId,
            Model model
    ) {
        return itemService.getItemById(itemId)
                .doOnNext(item -> model.addAttribute("item", item))
                .thenReturn(TEMPLATE_ADD_ITEM);
    }

    /**
     * Редактирование товара
     *
     * @param itemId      Идентификатор товара
     * @param title       Название товара
     * @param description Описание товара
     * @param image       Файл картинки товара (класс MultipartFile, может быть null - значит, остается прежним)
     * @param count       Количество товара
     * @param price       Цена товара
     * @return Редирект на отредактированный "/items/{id}"
     */
    @PostMapping("{id}/edit")
    public Mono<String> editItem(
            @PathVariable("id") Long itemId,
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestPart(required = false) MultipartFile image,
            @RequestParam(required = false) Integer count,
            @RequestParam(required = false) BigDecimal price
    ) {

        return itemService.editItem(itemId, title, description, image, count, price)
                .thenReturn(REDIRECT_ITEMS + itemId);
    }

    /**
     * Удаление товара
     *
     * @param itemId Идентификатор товара
     * @return Редирект на "/main/items"
     */
    @PostMapping(value = "/{id}/delete")
    public Mono<String> deleteItem(@PathVariable("id") Long itemId) {
        return itemService.deleteItem(itemId)
                .thenReturn(REDIRECT_MAIN_ITEMS);
    }

    /**
     * Изменение количества товара в корзине
     *
     * @param itemId Идентификатор товара
     * @param action Действие с товаром в корзине
     * @return Редирект на "/items/{id}"
     */
    @PostMapping("{id}")
    public Mono<String> changeItemCountInCart(
            @PathVariable("id") Long itemId,
            @RequestParam String action
    ) {
        return cartService.changeItemCountInCartByItemId(itemId, Action.forName(action))
                .thenReturn(REDIRECT_ITEMS + itemId);
    }
}
