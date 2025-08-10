package ru.yandex.practicum.payment.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.payment.service.PaymentService;
import ru.yandex.practicum.payment.service.model.BalanceResponse;
import ru.yandex.practicum.payment.service.model.PaymentRequest;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    @Value("${service.default-balance}")
    private BigDecimal defaultBalance;

    @Override
    public BalanceResponse getBalance() {
        return new BalanceResponse()
                .balance(defaultBalance);
    }

    @Override
    public Mono<Integer> makePayment(Mono<PaymentRequest> paymentRequest) {
        return paymentRequest
                .map(request -> request.getSum().compareTo(defaultBalance));
    }
}
