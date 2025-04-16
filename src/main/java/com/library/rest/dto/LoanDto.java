package com.library.rest.dto;

import java.time.ZonedDateTime;

public record LoanDto(Integer id, BookDto book, UserDto user, ZonedDateTime loanDate, ZonedDateTime dueDate,
                      ZonedDateTime returnDate) {
}
