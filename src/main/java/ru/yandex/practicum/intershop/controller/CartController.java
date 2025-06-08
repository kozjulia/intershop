package ru.yandex.practicum.intershop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.intershop.dto.Action;
import ru.yandex.practicum.intershop.dto.ItemDto;
import ru.yandex.practicum.intershop.service.CartService;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/cart/items")
@RequiredArgsConstructor
public class CartController extends RedirectController {

    private final CartService cartService;

    /**
     * @param model Модель
     * @return Шаблон "cart.html"
     */
    @GetMapping
    public String getCart(Model model) {

        List<ItemDto> items = cartService.getCart();
        BigDecimal total = items.stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getCount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("items", items);
        model.addAttribute("total", total);
        model.addAttribute("empty", items.isEmpty());

        return TEMPLATE_CART;
    }

    /**
     * @param itemId Идентификатор товара
     * @param action Действие с товаром в корзине
     * @return Редирект на "/cart/items"
     */
    @PostMapping("{id}")
    public String changeItemCountInCart(
            @PathVariable("id") Long itemId,
            @RequestParam String action
    ) {
        cartService.changeItemCountInCartByItemId(itemId, Action.forName(action));

        return REDIRECT_CART_ITEMS;
    }
}
