package ru.yandex.practicum.intershop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RedirectController {

    protected static final String TEMPLATE_CART = "/cart";
    protected static final String TEMPLATE_MAIN = "/main";
    protected static final String TEMPLATE_ITEM = "/item";
    protected static final String TEMPLATE_ADD_ITEM = "/add-item";
    protected static final String REDIRECT_ITEMS = "redirect:/items";
    protected static final String REDIRECT_CART_ITEMS = "redirect:/cart/items";
    protected static final String REDIRECT_MAIN_ITEMS = "redirect:/main/items";
    protected static final String SLASH = "/";

    /**
     * Редирект на "/main/items"
     *
     * @return Шаблон "main.html"
     */
    @GetMapping("/")
    public String redirect() {
        return REDIRECT_MAIN_ITEMS;
    }
}
