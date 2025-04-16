package com.library.mapper;

import com.library.repository.entity.BookEntity;
import com.library.rest.dto.BookDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookMapper extends BaseMapper<BookDto, BookEntity> {
}
