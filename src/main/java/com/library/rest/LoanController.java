package com.library.rest;

import com.library.rest.dto.LoanDto;
import com.library.rest.dto.LoanRequest;
import com.library.rest.dto.PaginatedResult;
import com.library.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/library/loans")
@Validated
public class LoanController {

    private final LoanService loanService;

    @GetMapping
    public PaginatedResult<LoanDto> getAllLoans(@RequestParam(defaultValue = "1") Integer page,
                                                @RequestParam(defaultValue = "10") Integer size) {

        return loanService.getAllLoans(page, size);
    }

    @PostMapping
    public LoanDto addLoan(@Valid @RequestBody final LoanRequest loanRequest) {

        return loanService.loanBook(loanRequest.bookId(), loanRequest.userId(), loanRequest.loanDays());
    }

    @PostMapping("/{loanId}/refund")
    public LoanDto refundLoan(@PathVariable Long loanId) {

        return loanService.refundBook(loanId);
    }
}
