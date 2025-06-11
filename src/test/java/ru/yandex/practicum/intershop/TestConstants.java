package ru.yandex.practicum.intershop;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.intershop.model.ItemEntity;

import java.math.BigDecimal;

@UtilityClass
public class TestConstants {

    public static final Long ITEM_ID = 77L;
    public static final String ITEM_TITLE = "Item title";
    public static final String ITEM_DESCRIPTION = "Item description";
    public static final String ITEM_IMAGE_PATH = "Item_image_path";
    public static final Integer ITEM_COUNT = 5;
    public static final BigDecimal ITEM_PRICE = BigDecimal.valueOf(550.50);

    public static final ItemEntity ITEM_ENTITY = ItemEntity.builder()
            .id(ITEM_ID)
            .title(ITEM_TITLE)
            .description(ITEM_DESCRIPTION)
            .imgPath(ITEM_IMAGE_PATH)
            .count(ITEM_COUNT)
            .price(ITEM_PRICE)
            .build();
}
