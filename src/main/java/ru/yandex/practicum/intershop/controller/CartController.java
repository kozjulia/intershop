package ru.yandex.practicum.intershop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.dto.Action;
import ru.yandex.practicum.intershop.dto.ActionRequest;
import ru.yandex.practicum.intershop.service.CartService;

import static ru.yandex.practicum.intershop.configuration.constants.TemplateConstants.REDIRECT_CART_ITEMS;
import static ru.yandex.practicum.intershop.configuration.constants.TemplateConstants.TEMPLATE_CART;

@Controller
@RequestMapping("/cart/items")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /**
     * Список товаров в корзине
     *
     * @param model Модель
     * @return Шаблон "cart.html"
     */
    @GetMapping
    public Mono<String> getCart(Model model) {
        return cartService.getCart()
                .collectList()
                .doOnNext(items -> {
                    java.math.BigDecimal total = items.stream()
                            .map(item -> item.getPrice().multiply(new java.math.BigDecimal(item.getCount())))
                            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
                    model.addAttribute("items", items);
                    model.addAttribute("total", total);
                    model.addAttribute("empty", items.isEmpty());
                })
                .thenReturn(TEMPLATE_CART);
    }

    /**
     * Изменение количества товара в корзине
     *
     * @param itemId        Идентификатор товара
     * @param actionRequest Действие с товаром в корзине
     * @return Редирект на "/cart/items"
     */
    @PostMapping("{id}")
    public Mono<String> changeItemCountInCart(
            @PathVariable("id") Long itemId,
            @ModelAttribute ActionRequest actionRequest
    ) {
        return cartService.changeItemCountInCartByItemId(itemId, Action.forName(actionRequest.action()))
                .thenReturn(REDIRECT_CART_ITEMS);
    }
}
