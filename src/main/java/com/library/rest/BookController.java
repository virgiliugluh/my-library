package com.library.rest;

import com.library.rest.dto.BookDto;
import com.library.rest.dto.PaginatedResult;
import com.library.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/library/books")
@Validated
public class BookController {

    private final BookService bookService;

    public BookController(@Qualifier("defaultBookService") BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/{bookId}")
    public BookDto getBookById(@PathVariable("bookId") Long bookId) {

        return bookService.getBookById(bookId);
    }

    @PostMapping
    public BookDto addBook(@Valid @RequestBody final BookDto book) {

        return bookService.addBook(book);
    }

    @DeleteMapping("/{bookId}")
    public void deleteBookById(@PathVariable("bookId") Long bookId) {

        bookService.deleteBookById(bookId);
    }

    @GetMapping
    public PaginatedResult<BookDto> getAllBooks(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size) {

        return bookService.getAllBooks(page, size);
    }
}
