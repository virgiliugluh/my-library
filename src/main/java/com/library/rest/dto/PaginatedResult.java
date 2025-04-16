package com.library.rest.dto;

import java.util.List;

public record PaginatedResult<T>(List<T> results, Long totalElements, Integer pageNumber, Integer pageSize) {
}
