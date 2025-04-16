package com.library.service;

import com.library.rest.dto.BookDto;
import com.library.rest.dto.PaginatedResult;

public interface BookService {

    BookDto getBookById(Long bookId);

    BookDto addBook(BookDto book);

    void deleteBookById(Long bookId);

    PaginatedResult<BookDto> getAllBooks(final Integer page, final Integer size);
}
