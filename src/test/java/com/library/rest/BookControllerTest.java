package com.library.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.exception.BookNotFoundException;
import com.library.rest.dto.BookDto;
import com.library.rest.dto.PaginatedResult;
import com.library.service.impl.DefaultBookService;
import org.instancio.Instancio;
import org.instancio.TypeToken;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DefaultBookService bookService;

    @Test
    void getBookById() throws Exception {

        final var bookDto = Instancio.create(BookDto.class);
        final var bookId = bookDto.id();
        when(bookService.getBookById(bookDto.id())).thenReturn(bookDto);

        final var result = mockMvc.perform(get("/library/books/{bookId}", bookId))
                .andExpect(status().isOk())
                .andReturn();

        final var actual = objectMapper.readValue(result.getResponse().getContentAsString(), BookDto.class);
        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(bookDto);
    }

    @Test
    void when_getBookById_then_return_404() throws Exception {

        final var bookId = 10L;
        when(bookService.getBookById(bookId)).thenThrow(new BookNotFoundException("Book not found"));

        mockMvc.perform(get("/library/books/{bookId}", bookId))
                .andExpect(status().isNotFound());
    }

    @Test
    void addBook() throws Exception {

        final var bookDto = Instancio.create(BookDto.class);
        final var bookDtoResponse = Instancio.create(BookDto.class);
        when(bookService.addBook(bookDto)).thenReturn(bookDtoResponse);

        final var result = mockMvc.perform(post("/library/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookDto)))
                .andExpect(status().isOk())
                .andReturn();

        final var actual = objectMapper.readValue(result.getResponse().getContentAsString(), BookDto.class);
        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(bookDtoResponse);
    }

    @Test
    void when_addBook_then_return_400() throws Exception {

        final var bookDto = new BookDto(10L, "test", null, null, false);

        mockMvc.perform(post("/library/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteBookById() throws Exception {

        final var bookId = 10L;
        doNothing().when(bookService).deleteBookById(bookId);

        mockMvc.perform(delete("/library/books/{bookId}", bookId))
                .andExpect(status().isOk());
    }

    @Test
    void getAllBooks() throws Exception {

        final var pageNumber = 1;
        final var pageSize = 10;
        final var paginatedResult = Instancio.create(new TypeToken<PaginatedResult<BookDto>>() {
        });
        when(bookService.getAllBooks(pageNumber, pageSize)).thenReturn(paginatedResult);

        final var result = mockMvc.perform(get("/library/books")
                        .param("page", String.valueOf(pageNumber))
                        .param("size", String.valueOf(pageSize)))
                .andExpect(status().isOk())
                .andReturn();

        final var actual = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<PaginatedResult<BookDto>>() {
        });
        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(paginatedResult);
    }
}