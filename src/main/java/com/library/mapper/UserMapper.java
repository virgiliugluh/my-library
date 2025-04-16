package com.library.mapper;

import com.library.repository.entity.UserEntity;
import com.library.rest.dto.UserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper extends BaseMapper<UserDto, UserEntity> {
}
