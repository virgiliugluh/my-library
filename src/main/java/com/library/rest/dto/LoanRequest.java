package com.library.rest.dto;

import jakarta.validation.constraints.NotNull;

public record LoanRequest(
        @NotNull(message = "BookId is mandatory")
        Long bookId,
        @NotNull(message = "UserId is mandatory")
        Long userId,
        @NotNull(message = "LoanDays is mandatory")
        Integer loanDays
) {
}
