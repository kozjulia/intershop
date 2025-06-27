package ru.yandex.practicum.intershop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.service.OrderService;

import static ru.yandex.practicum.intershop.configuration.constants.TemplateConstants.ORDERS;
import static ru.yandex.practicum.intershop.configuration.constants.TemplateConstants.TEMPLATE_ORDER;
import static ru.yandex.practicum.intershop.configuration.constants.TemplateConstants.TEMPLATE_ORDERS;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private static final String PARAM_NEW_ORDER_IS_TRUE = "?newOrder=true";

    private final OrderService orderService;

    /**
     * Покупка товаров из корзины (выполняет покупку товаров в корзине и очищает ее)
     *
     * @return Редирект на "/orders/{id}?newOrder=true"
     */
    @PostMapping("/buy")
    public Mono<Rendering> buyFromCart() {

        return orderService.createOrder()
                .map(orderId -> Rendering.redirectTo(ORDERS + orderId + PARAM_NEW_ORDER_IS_TRUE)
                        .build());
    }

    /**
     * Список заказов
     *
     * @param model Модель
     * @return Шаблон "orders.html"
     */
    @GetMapping("/orders")
    public Mono<Rendering> getOrders(Model model) {

        return orderService.findOrders()
                .collectList()
                .doOnNext(orders -> model.addAttribute("orders", orders))
                .thenReturn(Rendering.view(TEMPLATE_ORDERS)
                        .build());
    }

    /**
     * Карточка заказа
     *
     * @param orderId  Идентификатор заказа
     * @param newOrder true, если переход со страницы оформления заказа (по умолчанию, false)
     * @param model    Модель
     * @return Шаблон "order.html"
     */
    @GetMapping("/orders/{id}")
    public Mono<Rendering> getOrderById(
            @PathVariable("id") Long orderId,
            @RequestParam(required = false, defaultValue = "false") Boolean newOrder,
            Model model
    ) {

        return orderService.findOrderById(orderId)
                .doOnNext(order -> {
                    model.addAttribute("order", order);
                    model.addAttribute("newOrder", newOrder);
                })
                .thenReturn(Rendering.view(TEMPLATE_ORDER)
                        .build());
    }
}
