package com.library.service.impl;

import com.library.rest.dto.BookDto;
import com.library.rest.dto.PaginatedResult;
import com.library.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import static com.library.constants.CacheNames.ALL_BOOKS;
import static com.library.constants.CacheNames.BOOKS;

@Service
@RequiredArgsConstructor
public class CachedBookService implements BookService {

    private final DefaultBookService delegate;

    @Cacheable(cacheNames = BOOKS)
    @Override
    public BookDto getBookById(Long bookId) {

        return delegate.getBookById(bookId);
    }

    @CachePut(cacheNames = BOOKS, key = "#book.id")
    @Override
    public BookDto addBook(BookDto book) {

        return delegate.addBook(book);
    }

    @CacheEvict(cacheNames = BOOKS)
    @Override
    public void deleteBookById(Long bookId) {

        delegate.deleteBookById(bookId);
    }

    @Cacheable(cacheNames = ALL_BOOKS)
    @Override
    public PaginatedResult<BookDto> getAllBooks(Integer page, Integer size) {

        return delegate.getAllBooks(page, size);
    }
}
