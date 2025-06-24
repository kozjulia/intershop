package ru.yandex.practicum.intershop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

import static ru.yandex.practicum.intershop.configuration.constants.TemplateConstants.REDIRECT_MAIN_ITEMS;

@Controller
public class RedirectController {

    /**
     * Редирект на "/main/items"
     *
     * @return Шаблон "main.html"
     */
    @GetMapping("/")
    public Mono<String> redirect() {
        return Mono.just(REDIRECT_MAIN_ITEMS);
    }
}
