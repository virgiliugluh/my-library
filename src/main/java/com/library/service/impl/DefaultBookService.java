package com.library.service.impl;

import com.library.exception.BookNotFoundException;
import com.library.mapper.BookMapper;
import com.library.repository.BookRepository;
import com.library.rest.dto.BookDto;
import com.library.rest.dto.PaginatedResult;
import com.library.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static java.text.MessageFormat.format;

@RequiredArgsConstructor
@Slf4j
@Service
public class DefaultBookService implements BookService {

    private final BookMapper bookMapper;
    private final BookRepository bookRepository;

    @Override
    public BookDto getBookById(Long bookId) {

        return bookRepository.findById(bookId)
                .map(bookMapper::toDto)
                .orElseThrow(() -> new BookNotFoundException(format("Book with id {0} not found", bookId)));
    }

    @Override
    public BookDto addBook(BookDto book) {

        final var newBook = bookRepository.save(bookMapper.toEntity(book));
        log.info("New book added: {}", newBook);
        return bookMapper.toDto(newBook);
    }

    @Override
    public void deleteBookById(Long bookId) {

        bookRepository.deleteById(bookId);
        log.info("Book with id {} deleted", bookId);
    }

    @Override
    public PaginatedResult<BookDto> getAllBooks(Integer page, Integer size) {

        final var resultPage = bookRepository.findAll(Pageable.ofSize(size).withPage(page));
        final var items = resultPage
                .stream()
                .map(bookMapper::toDto)
                .toList();

        log.info("All books returned: {}", items);
        return new PaginatedResult<>(items, resultPage.getTotalElements(), resultPage.getNumber(), resultPage.getSize());
    }
}
