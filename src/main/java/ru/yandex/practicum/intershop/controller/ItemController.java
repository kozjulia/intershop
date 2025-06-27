package ru.yandex.practicum.intershop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.dto.Action;
import ru.yandex.practicum.intershop.dto.ActionRequest;
import ru.yandex.practicum.intershop.dto.ItemRequest;
import ru.yandex.practicum.intershop.service.CartService;
import ru.yandex.practicum.intershop.service.ItemService;

import static ru.yandex.practicum.intershop.configuration.constants.TemplateConstants.ITEMS;
import static ru.yandex.practicum.intershop.configuration.constants.TemplateConstants.MAIN_ITEMS;
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
    public Mono<Rendering> getItemById(@PathVariable("id") Long itemId, Model model) {
        return itemService.getItemById(itemId)
                .doOnNext(item -> model.addAttribute("item", item))
                .thenReturn(Rendering.view(TEMPLATE_ITEM)
                        .build());
    }

    /**
     * Страница добавления товара
     *
     * @param model Модель
     * @return Шаблон "add-item.html"
     */
    @GetMapping("/add")
    public Mono<Rendering> getAddingForm(Model model) {
        model.addAttribute("item", null);

        return Mono.just(Rendering.view(TEMPLATE_ADD_ITEM)
                .build());
    }

    /**
     * Добавление товара
     *
     * @param item Товар
     * @return Редирект на созданный "/items/{id}"
     */
    @PostMapping
    public Mono<Rendering> addItem(@ModelAttribute ItemRequest item) {

        return itemService.addItem(item.title(), item.description(), item.image(), item.count(), item.price())
                .map(itemId -> Rendering.redirectTo(ITEMS + itemId)
                        .build());
    }

    /**
     * Страница редактирования товара
     *
     * @param itemId Идентификатор товара
     * @param model  Модель
     * @return Редирект на форму редактирования товара "add-item.html"
     */
    @GetMapping("/{id}/edit")
    public Mono<Rendering> getEditingForm(@PathVariable("id") Long itemId, Model model) {
        return itemService.getItemById(itemId)
                .doOnNext(item -> model.addAttribute("item", item))
                .thenReturn(Rendering.view(TEMPLATE_ADD_ITEM)
                        .build());
    }

    /**
     * Редактирование товара
     *
     * @param itemId Идентификатор товара
     * @param item   Товар
     * @return Редирект на отредактированный "/items/{id}"
     */
    @PostMapping("{id}/edit")
    public Mono<Rendering> editItem(
            @PathVariable("id") Long itemId,
            @ModelAttribute ItemRequest item
    ) {

        return itemService.editItem(itemId, item.title(), item.description(), item.image(), item.count(), item.price())
                .map(id -> Rendering.redirectTo(ITEMS + itemId)
                        .build());
    }

    /**
     * Удаление товара
     *
     * @param itemId Идентификатор товара
     * @return Редирект на "/main/items"
     */
    @PostMapping(value = "/{id}/delete")
    public Mono<Rendering> deleteItem(@PathVariable("id") Long itemId) {
        return itemService.deleteItem(itemId)
                .thenReturn(Rendering.redirectTo(MAIN_ITEMS)
                        .build());
    }

    /**
     * Изменение количества товара в корзине
     *
     * @param itemId        Идентификатор товара
     * @param actionRequest Действие с товаром в корзине
     * @return Редирект на "/items/{id}"
     */
    @PostMapping("{id}")
    public Mono<Rendering> changeItemCountInCart(
            @PathVariable("id") Long itemId,
            @ModelAttribute ActionRequest actionRequest
    ) {
        return Mono.fromRunnable(() -> cartService.changeItemCountInCartByItemId(itemId, Action.forName(actionRequest.action())))
                .thenReturn(Rendering.redirectTo(ITEMS + itemId)
                        .build());
    }
}
