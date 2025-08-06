package ru.yandex.practicum.store.showcase.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.store.showcase.client.ApiClient;
import ru.yandex.practicum.store.showcase.client.api.PaymentApi;

@Configuration
public class PaymentClientConfiguration {

    @Bean
    public PaymentApi paymentApi(@Value("${PAYMENT_CLIENT_HOST:localhost}") String restHost, @Value("${PAYMENT_CLIENT_PORT:8088}") int restPort) {
        return new PaymentApi(new ApiClient().setBasePath("http://" + restHost + ":" + restPort));
    }
}
