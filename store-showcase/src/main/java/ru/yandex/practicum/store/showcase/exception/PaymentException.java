package ru.yandex.practicum.store.showcase.exception;

public class PaymentException extends RuntimeException {

    public PaymentException(String message, Throwable cause) {
        super(message, cause);
    }
}
