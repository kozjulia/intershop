package ru.yandex.practicum.intershop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.dto.Action;
import ru.yandex.practicum.intershop.dto.ActionRequest;
import ru.yandex.practicum.intershop.dto.ItemSort;
import ru.yandex.practicum.intershop.dto.PagingDto;
import ru.yandex.practicum.intershop.service.CartService;
import ru.yandex.practicum.intershop.service.ItemService;

import static ru.yandex.practicum.intershop.configuration.constants.TemplateConstants.MAIN_ITEMS;
import static ru.yandex.practicum.intershop.configuration.constants.TemplateConstants.TEMPLATE_MAIN;

@Controller
@RequestMapping("/main/items")
@RequiredArgsConstructor
public class MainController {

    private final CartService cartService;
    private final ItemService itemService;

    /**
     * Список всех товаров плиткой на главной странице
     *
     * @param search     Строка с поисков по названию/описанию товара (по умолчанию, пустая строка - все товары)
     * @param sort       Сортировка перечисление NO, ALPHA, PRICE (по умолчанию, NO - не использовать сортировку)
     * @param pageSize   Максимальное число товаров на странице (по умолчанию, 10)
     * @param pageNumber Номер текущей страницы (по умолчанию, 1)
     * @param model      Модель
     * @return Шаблон "main.html"
     */
    @GetMapping
    public Mono<Rendering> getMainPage(
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "NO") ItemSort sort,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false, defaultValue = "1") Integer pageNumber,
            Model model
    ) {
        return itemService.findAllItemsPagingAndSorting(search, sort, pageSize, pageNumber)
                .collectList()
                .doOnNext(items -> {
                    model.addAttribute("items", items);
                    model.addAttribute("search", search);
                    model.addAttribute("sort", sort);
                    model.addAttribute("paging", new PagingDto(pageNumber, pageSize, items.size()));
                })
                .thenReturn(Rendering.view(TEMPLATE_MAIN)
                        .build());
    }

    /**
     * Изменение количества товара в корзине
     *
     * @param itemId        Идентификатор товара
     * @param actionRequest Действие с товаром в корзине
     * @return Редирект на "/main/items"
     */
    @PostMapping("{id}")
    public Mono<Rendering> changeItemCountInCart(
            @PathVariable("id") Long itemId,
            @ModelAttribute ActionRequest actionRequest
    ) {
        return Mono.fromRunnable(() -> cartService.changeItemCountInCartByItemId(itemId, Action.forName(actionRequest.action())))
                .thenReturn(Rendering.redirectTo(MAIN_ITEMS)
                        .build());
    }
}
