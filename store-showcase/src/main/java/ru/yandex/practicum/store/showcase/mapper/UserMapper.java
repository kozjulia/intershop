package ru.yandex.practicum.store.showcase.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.store.showcase.dto.UserDto;
import ru.yandex.practicum.store.showcase.model.UserEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    UserDto toUserDto(UserEntity userEntity);
}
