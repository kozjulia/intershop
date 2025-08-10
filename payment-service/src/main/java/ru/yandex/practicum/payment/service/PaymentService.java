package ru.yandex.practicum.payment.service;

import reactor.core.publisher.Mono;
import ru.yandex.practicum.payment.service.model.BalanceResponse;
import ru.yandex.practicum.payment.service.model.PaymentRequest;

public interface PaymentService {

    BalanceResponse getBalance();

    Mono<Integer> makePayment(Mono<PaymentRequest> paymentRequest);
}
