package ru.yandex.practicum.intershop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.intershop.dto.ItemDto;
import ru.yandex.practicum.intershop.dto.PagingDto;
import ru.yandex.practicum.intershop.dto.ItemSort;
import ru.yandex.practicum.intershop.service.ItemService;

import java.util.List;

@Controller
@RequestMapping("/main")
@RequiredArgsConstructor
public class MainController extends RedirectController {

    private final ItemService itemService;

    /**
     * @param search     Строка с поисков по названию/описанию товара (по умолчанию, пустая строка - все товары)
     * @param sort       Сортировка перечисление NO, ALPHA, PRICE (по умолчанию, NO - не использовать сортировку)
     * @param pageSize   Максимальное число товаров на странице (по умолчанию, 10)
     * @param pageNumber Номер текущей страницы (по умолчанию, 1)
     * @param model      Модель
     * @return Шаблон "main.html"
     */
    @GetMapping("/items")
    public String getMainPage(
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "NO") ItemSort sort,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false, defaultValue = "1") Integer pageNumber,
            Model model
    ) {

        List<ItemDto> items = itemService.findAllItemsPagingAndSorting(search, sort, pageSize, pageNumber);

        model.addAttribute("items", items);
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);
        model.addAttribute("paging", new PagingDto(pageNumber, pageSize, items.size()));

        return TEMPLATE_MAIN;
    }
}
