package com.library.service.impl;

import com.library.exception.BookAlreadyLoanedException;
import com.library.exception.BookNotFoundException;
import com.library.exception.LoanNotFoundException;
import com.library.exception.UserNotFoundException;
import com.library.repository.BookRepository;
import com.library.repository.LoanRepository;
import com.library.repository.UserRepository;
import com.library.repository.entity.BookEntity;
import com.library.repository.entity.LoanEntity;
import com.library.repository.entity.UserEntity;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static java.text.MessageFormat.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultLoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DefaultLoanService defaultLoanService;

    @Test
    void loanBook() {

        final var userId = 10L;
        final var userEntity = Instancio.create(UserEntity.class);
        final var bookId = 100L;
        final var bookEntity = Instancio.create(BookEntity.class);
        bookEntity.setIsLoaned(false);
        final var loanDays = 30;
        final var loan = LoanEntity.builder()
                .book(bookEntity)
                .user(userEntity)
                .loanDate(ZonedDateTime.now())
                .dueDate(ZonedDateTime.now().plusDays(loanDays))
                .build();
        final var loanCaptor = ArgumentCaptor.forClass(LoanEntity.class);
        final var loanedBookEntity = bookEntity.toBuilder().isLoaned(true).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(bookRepository.findByIdForUpdate(bookId)).thenReturn(bookEntity);
        when(loanRepository.save(loanCaptor.capture())).thenReturn(loan);
        when(bookRepository.save(loanedBookEntity)).thenReturn(bookEntity);

        final var savedLoanDto = defaultLoanService.loanBook(userId, bookId, loanDays);
        final var capturedLoanDto = loanCaptor.getValue();
        assertThat(savedLoanDto.loanDate()).isEqualTo(capturedLoanDto.getLoanDate());
        assertThat(savedLoanDto.dueDate()).isEqualTo(capturedLoanDto.getDueDate());
        assertThat(savedLoanDto.returnDate()).isNull();

        final var bookDto = savedLoanDto.book();
        assertThat(bookDto.id()).isEqualTo(bookEntity.getId());
        assertThat(bookDto.title()).isEqualTo(bookEntity.getTitle());
        assertThat(bookDto.author()).isEqualTo(bookEntity.getAuthor());
        assertThat(bookDto.isbn()).isEqualTo(bookEntity.getIsbn());
        assertThat(bookDto.isLoaned()).isEqualTo(bookEntity.getIsLoaned());

        final var userDto = savedLoanDto.user();
        assertThat(userDto.id()).isEqualTo(userEntity.getId());
        assertThat(userDto.firstName()).isEqualTo(userEntity.getFirstName());
        assertThat(userDto.lastName()).isEqualTo(userEntity.getLastName());
        assertThat(userDto.email()).isEqualTo(userEntity.getEmail());

        verify(userRepository, times(1)).findById(userId);
        verify(bookRepository, times(1)).findByIdForUpdate(bookId);
        verify(loanRepository, times(1)).save(any(LoanEntity.class));
        verify(bookRepository, times(1)).save(loanedBookEntity);
    }

    @Test
    void when_loanBook_then_throw_userNotFoundException() {

        final var userId = 10L;
        final var bookId = 100L;
        final var loanDays = 30;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> defaultLoanService.loanBook(userId, bookId, loanDays))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(format("User with id {0} not found", userId));

        verify(userRepository, times(1)).findById(userId);
        verifyNoMoreInteractions(bookRepository);
        verifyNoMoreInteractions(loanRepository);
    }

    @Test
    void when_loanBook_then_throw_bookNotFoundException() {

        final var userId = 10L;
        final var bookId = 100L;
        final var loanDays = 30;

        when(userRepository.findById(userId)).thenReturn(Optional.of(mock(UserEntity.class)));
        when(bookRepository.findByIdForUpdate(userId)).thenReturn(null);

        assertThatThrownBy(() -> defaultLoanService.loanBook(userId, bookId, loanDays))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining(format("Book with id {0} not found", bookId));

        verify(userRepository, times(1)).findById(userId);
        verify(bookRepository, times(1)).findByIdForUpdate(bookId);
        verifyNoMoreInteractions(loanRepository);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    void when_loanBook_then_throw_bookAlreadyLoanedException() {

        final var userId = 10L;
        final var bookId = 100L;
        final var loanDays = 30;

        when(userRepository.findById(userId)).thenReturn(Optional.of(mock(UserEntity.class)));
        when(bookRepository.findByIdForUpdate(bookId)).thenReturn(BookEntity.builder().isLoaned(true).build());

        assertThatThrownBy(() -> defaultLoanService.loanBook(userId, bookId, loanDays))
                .isInstanceOf(BookAlreadyLoanedException.class)
                .hasMessageContaining(format("Book with id {0} is already loaned", bookId));

        verify(userRepository, times(1)).findById(userId);
        verify(bookRepository, times(1)).findByIdForUpdate(bookId);
        verifyNoMoreInteractions(loanRepository);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    void refundBook() {

        final var loanId = 10L;
        final var userEntity = Instancio.create(UserEntity.class);
        final var bookEntity = Instancio.create(BookEntity.class);
        final var bookId = bookEntity.getId();
        bookEntity.setIsLoaned(true);
        final var loanDays = 30;
        final var loanEntity = LoanEntity.builder()
                .book(bookEntity)
                .user(userEntity)
                .loanDate(ZonedDateTime.now())
                .dueDate(ZonedDateTime.now().plusDays(loanDays))
                .build();
        final var loanCaptor = ArgumentCaptor.forClass(LoanEntity.class);
        final var notLoanedBookEntity = bookEntity.toBuilder().isLoaned(false).build();

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loanEntity));
        when(bookRepository.findByIdForUpdate(bookId)).thenReturn(bookEntity);
        when(loanRepository.save(loanCaptor.capture())).thenReturn(loanEntity);
        when(bookRepository.save(notLoanedBookEntity)).thenReturn(bookEntity);

        final var savedLoanDto = defaultLoanService.refundBook(loanId);
        final var capturedLoanDto = loanCaptor.getValue();
        assertThat(savedLoanDto.loanDate()).isEqualTo(capturedLoanDto.getLoanDate());
        assertThat(savedLoanDto.dueDate()).isEqualTo(capturedLoanDto.getDueDate());
        assertThat(savedLoanDto.returnDate()).isEqualTo(capturedLoanDto.getReturnDate());

        final var bookDto = savedLoanDto.book();
        assertThat(bookDto.id()).isEqualTo(bookEntity.getId());
        assertThat(bookDto.title()).isEqualTo(bookEntity.getTitle());
        assertThat(bookDto.author()).isEqualTo(bookEntity.getAuthor());
        assertThat(bookDto.isbn()).isEqualTo(bookEntity.getIsbn());
        assertThat(bookDto.isLoaned()).isEqualTo(bookEntity.getIsLoaned());

        final var userDto = savedLoanDto.user();
        assertThat(userDto.id()).isEqualTo(userEntity.getId());
        assertThat(userDto.firstName()).isEqualTo(userEntity.getFirstName());
        assertThat(userDto.lastName()).isEqualTo(userEntity.getLastName());
        assertThat(userDto.email()).isEqualTo(userEntity.getEmail());

        verifyNoInteractions(userRepository);
        verify(bookRepository, times(1)).findByIdForUpdate(bookId);
        verify(loanRepository, times(1)).findById(loanId);
        verify(loanRepository, times(1)).save(any(LoanEntity.class));
        verify(bookRepository, times(1)).save(notLoanedBookEntity);
    }

    @Test
    void when_refundBook_then_throw_loanNotFoundException() {

        final var loanId = 10L;

        when(loanRepository.findById(loanId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> defaultLoanService.refundBook(loanId))
                .isInstanceOf(LoanNotFoundException.class)
                .hasMessageContaining(format("Loan with id {0} not found", loanId));

        verify(loanRepository, times(1)).findById(loanId);
        verifyNoMoreInteractions(loanRepository);
        verifyNoInteractions(userRepository);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    void getAllLoans() {

        final var pageable = Pageable.ofSize(100).withPage(1);
        final var loanEntity = Instancio.create(LoanEntity.class);

        when(loanRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(loanEntity), pageable, 1L));

        final var paginatedResult = defaultLoanService.getAllLoans(pageable.getPageNumber(), pageable.getPageSize());

        assertThat(paginatedResult.results().size()).isEqualTo(1);
        assertThat(paginatedResult.pageNumber()).isEqualTo(pageable.getPageNumber());
        assertThat(paginatedResult.pageSize()).isEqualTo(pageable.getPageSize());

        final var loanDto = paginatedResult.results().getFirst();
        assertThat(loanDto.loanDate()).isEqualTo(loanEntity.getLoanDate());
        assertThat(loanDto.dueDate()).isEqualTo(loanEntity.getDueDate());
        assertThat(loanDto.returnDate()).isEqualTo(loanEntity.getReturnDate());

        final var bookDto = loanDto.book();
        final var bookEntity = loanEntity.getBook();
        assertThat(bookDto.id()).isEqualTo(bookEntity.getId());
        assertThat(bookDto.title()).isEqualTo(bookEntity.getTitle());
        assertThat(bookDto.author()).isEqualTo(bookEntity.getAuthor());
        assertThat(bookDto.isbn()).isEqualTo(bookEntity.getIsbn());
        assertThat(bookDto.isLoaned()).isEqualTo(bookEntity.getIsLoaned());

        final var userDto = loanDto.user();
        final var userEntity = loanEntity.getUser();
        assertThat(userDto.id()).isEqualTo(userEntity.getId());
        assertThat(userDto.firstName()).isEqualTo(userEntity.getFirstName());
        assertThat(userDto.lastName()).isEqualTo(userEntity.getLastName());
        assertThat(userDto.email()).isEqualTo(userEntity.getEmail());

        verify(loanRepository, times(1)).findAll(pageable);
        verifyNoMoreInteractions(loanRepository);
        verifyNoInteractions(userRepository);
        verifyNoMoreInteractions(bookRepository);
    }
}