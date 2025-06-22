package ru.yandex.practicum.intershop.model;

import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class SequenceGenerator {

    private final DatabaseClient client;

    public Mono<Long> generateItemId() {
        return client.sql("SELECT nextval('SEQ_ITEM')")
                .map(row -> row.get(0, Long.class))
                .one();
    }

    public Mono<Long> generateOrderId() {
        return client.sql("SELECT nextval('SEQ_ORDER')")
                .map(row -> row.get(0, Long.class))
                .one();
    }
}
