package ru.yandex.practicum.intershop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

import static ru.yandex.practicum.intershop.configuration.constants.TemplateConstants.MAIN_ITEMS;

@Controller
public class RedirectController {

    /**
     * Редирект на "/main/items"
     *
     * @return Шаблон "main.html"
     */
    @GetMapping("/")
    public Mono<Rendering> redirect() {
        return Mono.just(Rendering.redirectTo(MAIN_ITEMS)
                .build());
    }
}
