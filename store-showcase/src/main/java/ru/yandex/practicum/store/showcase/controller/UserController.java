package ru.yandex.practicum.store.showcase.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.store.showcase.dto.UserDto;
import ru.yandex.practicum.store.showcase.service.UserService;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/login")
    public Mono<String> getFormLogin() {
        return Mono.just("login");
    }

    @GetMapping("/logout")
    public Mono<String> getFormLogout() {
        return Mono.just("logout");
    }

    @GetMapping("/register")
    public Mono<String> getFormRegister() {
        return Mono.just("register");
    }

    @PostMapping("/register")
    public Mono<String> registerUser(Model model, UserDto userDto, ServerWebExchange exchange) {
        return userService.registerUser(userDto, exchange)
                .onErrorResume(IllegalArgumentException.class, err -> {
                    model.addAttribute("registerError", err.getMessage());
                    return Mono.just("register");
                })
                .onErrorResume(Exception.class, err -> {
                    model.addAttribute("registerError", "Ошибка регистрации, попробуйте позже");
                    return Mono.just("register");
                });
    }
}
