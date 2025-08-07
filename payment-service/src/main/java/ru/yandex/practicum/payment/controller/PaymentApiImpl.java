package ru.yandex.practicum.payment.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.payment.service.api.PaymentApi;
import ru.yandex.practicum.payment.service.model.BalanceResponse;
import ru.yandex.practicum.payment.service.model.PaymentRequest;

import java.math.BigDecimal;

@Controller
public class PaymentApiImpl implements PaymentApi {

    @Value("${service.default-balance}")
    private BigDecimal defaultBalance;

    @Override
    public Mono<ResponseEntity<BalanceResponse>> getBalance(final ServerWebExchange exchange) {
        BalanceResponse response = new BalanceResponse()
                .balance(defaultBalance);
        return Mono.just(ResponseEntity.ok(response));
    }

    @Override
    public Mono<ResponseEntity<Void>> makePayment(
            Mono<PaymentRequest> paymentRequest,
            final ServerWebExchange exchange
    ) {
        return paymentRequest
                .flatMap(request -> {
                    int comparison = request.getSum().compareTo(defaultBalance);
                    if (comparison <= 0) {
                        return Mono.just(ResponseEntity.ok().build());
                    } else {
                        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).build());
                    }
                });
    }
}
