package ru.yandex.practicum.payment.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.yandex.practicum.payment.service.model.BalanceResponse;
import ru.yandex.practicum.payment.service.model.PaymentRequest;

import java.math.BigDecimal;


@WebFluxTest(PaymentApiImpl.class)
@TestPropertySource(properties = "service.default-balance=3000")
class PaymentApiImplTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void getBalanceSuccessfulTest() {

        BalanceResponse expectedBalance = new BalanceResponse();
        expectedBalance.setBalance(BigDecimal.valueOf(3000));

        webTestClient
                .get()
                .uri("/balance")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json")
                .expectBody(BalanceResponse.class)
                .isEqualTo(expectedBalance);
    }

    @Test
    void makePaymentSuccessfulTest() {

        PaymentRequest request = new PaymentRequest();
        request.setSum(BigDecimal.valueOf(1000));

        webTestClient
                .post()
                .uri("/balance")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();
    }

    @Test
    void makePaymentWithStatusConflictTest() {

        PaymentRequest request = new PaymentRequest();
        request.setSum(BigDecimal.valueOf(5000));

        webTestClient
                .post()
                .uri("/balance")
                .bodyValue(request)
                .exchange()
                .expectStatus().isEqualTo(HttpStatusCode.valueOf(409))
                .expectBody().isEmpty();
    }
}