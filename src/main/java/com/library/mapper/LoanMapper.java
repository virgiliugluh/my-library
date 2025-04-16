package com.library.mapper;

import com.library.repository.entity.LoanEntity;
import com.library.rest.dto.LoanDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LoanMapper extends BaseMapper<LoanDto, LoanEntity> {
}
