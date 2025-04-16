package com.library.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record BookDto(
        Long id,
        @NotBlank(message = "Title is mandatory")
        String title,
        @NotBlank(message = "Author is mandatory")
        String author,
        @NotBlank(message = "ISBN is mandatory")
        String isbn,
        Boolean isLoaned) {
}
