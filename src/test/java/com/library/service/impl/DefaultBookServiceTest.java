package com.library.service.impl;

import com.library.exception.BookNotFoundException;
import com.library.mapper.BookMapper;
import com.library.mapper.BookMapperImpl;
import com.library.repository.BookRepository;
import com.library.repository.entity.BookEntity;
import com.library.rest.dto.BookDto;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static java.text.MessageFormat.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultBookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Spy
    private BookMapper bookMapper = new BookMapperImpl();

    @InjectMocks
    private DefaultBookService defaultBookService;

    @Test
    void getBookById() {

        final var bookId = 1L;
        final var bookEntity = Instancio.create(BookEntity.class);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(bookEntity));

        final var bookDto = defaultBookService.getBookById(bookId);

        assertThat(bookDto.id()).isEqualTo(bookEntity.getId());
        assertThat(bookDto.title()).isEqualTo(bookEntity.getTitle());
        assertThat(bookDto.author()).isEqualTo(bookEntity.getAuthor());
        assertThat(bookDto.isbn()).isEqualTo(bookEntity.getIsbn());
        assertThat(bookDto.isLoaned()).isEqualTo(bookEntity.getIsLoaned());

        verify(bookRepository, times(1)).findById(bookId);
    }

    @Test
    void when_getBookById_then_throw_exception() {

        final var bookId = 1L;

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> defaultBookService.getBookById(bookId))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining(format("Book with id {0} not found", bookId));

        verify(bookRepository, times(1)).findById(bookId);
    }

    @Test
    void addBook() {

        final var bookDto = Instancio.create(BookDto.class);
        final var bookEntity = bookMapper.toEntity(bookDto);

        when(bookRepository.save(bookEntity)).thenReturn(bookEntity);

        final var savedBookDto = defaultBookService.addBook(bookDto);

        assertThat(savedBookDto.id()).isEqualTo(bookEntity.getId());
        assertThat(savedBookDto.title()).isEqualTo(bookEntity.getTitle());
        assertThat(savedBookDto.author()).isEqualTo(bookEntity.getAuthor());
        assertThat(savedBookDto.isbn()).isEqualTo(bookEntity.getIsbn());
        assertThat(savedBookDto.isLoaned()).isEqualTo(bookEntity.getIsLoaned());

        verify(bookRepository, times(1)).save(bookEntity);
    }

    @Test
    void deleteBookById() {

        final var bookId = 1L;

        doNothing().when(bookRepository).deleteById(bookId);

        defaultBookService.deleteBookById(bookId);

        verify(bookRepository, times(1)).deleteById(bookId);
    }

    @Test
    void getAllBooks() {

        final var pageable = Pageable.ofSize(100).withPage(1);
        final var bookEntity = Instancio.create(BookEntity.class);

        when(bookRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(bookEntity), pageable, 1L));

        final var paginatedResult = defaultBookService.getAllBooks(pageable.getPageNumber(), pageable.getPageSize());

        assertThat(paginatedResult.results().size()).isEqualTo(1);
        assertThat(paginatedResult.pageNumber()).isEqualTo(pageable.getPageNumber());
        assertThat(paginatedResult.pageSize()).isEqualTo(pageable.getPageSize());

        final var bookDto = paginatedResult.results().getFirst();

        assertThat(bookDto.id()).isEqualTo(bookEntity.getId());
        assertThat(bookDto.title()).isEqualTo(bookEntity.getTitle());
        assertThat(bookDto.author()).isEqualTo(bookEntity.getAuthor());
        assertThat(bookDto.isbn()).isEqualTo(bookEntity.getIsbn());
        assertThat(bookDto.isLoaned()).isEqualTo(bookEntity.getIsLoaned());

        verify(bookRepository, times(1)).findAll(pageable);
    }
}