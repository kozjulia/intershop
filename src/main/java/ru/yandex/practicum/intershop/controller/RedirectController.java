package ru.yandex.practicum.intershop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class RedirectController {

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
