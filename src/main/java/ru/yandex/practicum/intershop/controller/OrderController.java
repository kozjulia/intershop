package ru.yandex.practicum.intershop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.intershop.dto.OrderDto;
import ru.yandex.practicum.intershop.service.OrderService;

import java.util.List;

import static ru.yandex.practicum.intershop.configuration.constants.TemplateConstants.REDIRECT_ORDERS;
import static ru.yandex.practicum.intershop.configuration.constants.TemplateConstants.SLASH;
import static ru.yandex.practicum.intershop.configuration.constants.TemplateConstants.TEMPLATE_ORDER;
import static ru.yandex.practicum.intershop.configuration.constants.TemplateConstants.TEMPLATE_ORDERS;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * Покупка товаров из корзины (выполняет покупку товаров в корзине и очищает ее)
     *
     * @return Редирект на "/orders/{id}?newOrder=true"
     */
    @PostMapping("/buy")
    public String buyFromCart() {

        Long orderId = orderService.createOrder();

        return REDIRECT_ORDERS + SLASH + orderId + "?newOrder=true";
    }

    /**
     * Список заказов
     *
     * @param model Модель
     * @return Шаблон "orders.html"
     */
    @GetMapping("/orders")
    public String getOrders(Model model) {

        List<OrderDto> orders = orderService.findOrders();

        model.addAttribute("orders", orders);

        return TEMPLATE_ORDERS;
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
    public String getOrderById(
            @PathVariable("id") Long orderId,
            @RequestParam(required = false, defaultValue = "false") Boolean newOrder,
            Model model
    ) {

        OrderDto order = orderService.findOrderById(orderId);

        model.addAttribute("order", order);
        model.addAttribute("newOrder", newOrder);

        return TEMPLATE_ORDER;
    }
}
