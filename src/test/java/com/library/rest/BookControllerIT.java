package com.library.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.repository.BookRepository;
import com.library.rest.dto.BookDto;
import com.library.rest.dto.PaginatedResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class, LiquibaseAutoConfiguration.class})
class BookControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookRepository bookRepository;

    @Test
    void getBookById() throws Exception {


        final var result = mockMvc.perform(get("/library/books/{bookId}", 10L))
                .andExpect(status().isOk())
                .andReturn();

        final var expected = new BookDto(
                1000L,
                "Patterns of Enterprise Application Architecture",
                "Fowler Martin",
                "B008OHVDFM",
                false
        );

        final var actual = objectMapper.readValue(result.getResponse().getContentAsString(), BookDto.class);
        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void when_getBookById_then_return_404() throws Exception {

        final var bookId = 100L;

        mockMvc.perform(get("/library/books/{bookId}", bookId))
                .andExpect(status().isNotFound());
    }

    @Test
    void addBook() throws Exception {

        final var bookDto = new BookDto(
                null,
                "Test Title",
                "Test Author",
                "isbn123",
                false
        );

        final var result = mockMvc.perform(post("/library/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookDto)))
                .andExpect(status().isOk())
                .andReturn();

        final var actual = objectMapper.readValue(result.getResponse().getContentAsString(), BookDto.class);
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(bookDto);
    }

    @Test
    void deleteBookById() throws Exception {

        final var bookId = 2000L;

        final var bookEntity1 = bookRepository.findById(bookId);
        assertThat(bookEntity1).isNotEmpty();

        mockMvc.perform(delete("/library/books/{bookId}", bookId))
                .andExpect(status().isOk());

        final var bookEntity2 = bookRepository.findById(bookId);
        assertThat(bookEntity2).isEmpty();
    }

    @Test
    void getAllBooks() throws Exception {

        final var pageNumber = 0;
        final var pageSize = 100;

        final var result = mockMvc.perform(get("/library/books")
                        .param("page", String.valueOf(pageNumber))
                        .param("size", String.valueOf(pageSize)))
                .andExpect(status().isOk())
                .andReturn();

        final var actual = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<PaginatedResult<BookDto>>() {
        });
        assertThat(actual.results()).hasSize(2);
    }
}