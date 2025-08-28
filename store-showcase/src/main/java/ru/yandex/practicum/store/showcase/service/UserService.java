package ru.yandex.practicum.store.showcase.service;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.store.showcase.dto.UserDto;

public interface UserService {

    Mono<UserDto> findByName(String name);

    Mono<String> registerUser(UserDto userDto, ServerWebExchange exchange);
}
