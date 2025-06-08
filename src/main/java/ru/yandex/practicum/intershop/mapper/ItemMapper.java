package ru.yandex.practicum.intershop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.intershop.dto.ItemDto;
import ru.yandex.practicum.intershop.model.ItemEntity;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ItemMapper {

    ItemDto toItemDto(ItemEntity itemEntity);

    List<ItemDto> toItemDtos(List<ItemEntity> itemEntities);
}
