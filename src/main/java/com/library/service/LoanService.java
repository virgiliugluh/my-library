package com.library.service;

import com.library.rest.dto.LoanDto;
import com.library.rest.dto.PaginatedResult;

public interface LoanService {

    LoanDto loanBook(Long bookId, Long userId, Integer loanDays);

    LoanDto refundBook(Long loanId);

    PaginatedResult<LoanDto> getAllLoans(final Integer page, final Integer size);
}
