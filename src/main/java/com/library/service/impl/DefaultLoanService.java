package com.library.service.impl;

import com.library.exception.BookAlreadyLoanedException;
import com.library.exception.BookNotFoundException;
import com.library.exception.LoanNotFoundException;
import com.library.exception.UserNotFoundException;
import com.library.mapper.LoanMapper;
import com.library.repository.BookRepository;
import com.library.repository.LoanRepository;
import com.library.repository.UserRepository;
import com.library.repository.entity.LoanEntity;
import com.library.rest.dto.LoanDto;
import com.library.rest.dto.PaginatedResult;
import com.library.service.LoanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

import static java.text.MessageFormat.format;

@Slf4j
@RequiredArgsConstructor
@Service
public class DefaultLoanService implements LoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final LoanMapper loanMapper;

    @Override
    @Transactional
    public LoanDto loanBook(Long bookId, Long userId, Integer loanDays) {

        final var book = bookRepository.findByIdForUpdate(bookId);
        if (book == null) {
            throw new BookNotFoundException(format("Book with id {0} not found", bookId));
        }
        if (book.getIsLoaned()) {
            throw new BookAlreadyLoanedException(format("Book with id {0} is already loaned", bookId));
        }

        final var user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(format("User with id {0} not found", userId)));

        final var loan = LoanEntity.builder()
                .book(book)
                .user(user)
                .loanDate(ZonedDateTime.now())
                .dueDate(ZonedDateTime.now().plusDays(loanDays))
                .build();
        loanRepository.save(loan);

        book.setIsLoaned(true);
        bookRepository.save(book);

        log.info("Book with id {} has been loaned", bookId);
        return loanMapper.toDto(loan);
    }

    @Override
    @Transactional
    public LoanDto refundBook(Long loanId) {

        final var loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException(format("Loan with id {0} not found", loanId)));
        final var book = bookRepository.findByIdForUpdate(loan.getBook().getId());

        loan.setReturnDate(ZonedDateTime.now());
        loanRepository.save(loan);

        book.setIsLoaned(false);
        bookRepository.save(book);

        log.info("Book with id {} has been refunded", book.getId());
        return loanMapper.toDto(loan);
    }


    @Override
    public PaginatedResult<LoanDto> getAllLoans(Integer page, Integer size) {

        final var resultPage = loanRepository.findAll(Pageable.ofSize(size).withPage(page));
        final var items = resultPage
                .stream().map(loanMapper::toDto)
                .toList();

        log.info("All loans returned: {}", items);
        return new PaginatedResult<>(items, resultPage.getTotalElements(), resultPage.getNumber(), resultPage.getSize());
    }
}
