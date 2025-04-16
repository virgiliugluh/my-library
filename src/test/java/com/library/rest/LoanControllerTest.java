package com.library.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.exception.LoanNotFoundException;
import com.library.rest.dto.LoanDto;
import com.library.rest.dto.LoanRequest;
import com.library.rest.dto.PaginatedResult;
import com.library.service.impl.DefaultLoanService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DefaultLoanService loanService;

    @Test
    void getAllLoans() throws Exception {

        final var pageNumber = 1;
        final var pageSize = 10;
        final var paginatedResult = Instancio.create(new TypeToken<PaginatedResult<LoanDto>>() {
        });
        when(loanService.getAllLoans(pageNumber, pageSize)).thenReturn(paginatedResult);

        final var result = mockMvc.perform(get("/library/loans")
                        .param("page", String.valueOf(pageNumber))
                        .param("size", String.valueOf(pageSize)))
                .andExpect(status().isOk())
                .andReturn();

        final var actual = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<PaginatedResult<LoanDto>>() {
        });
        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(paginatedResult);
    }

    @Test
    void addLoan() throws Exception {

        final var userId = 10L;
        final var bookId = 100L;
        final var loanDays = 30;
        final var loanRequest = new LoanRequest(userId, bookId, loanDays);
        final var loanDto = Instancio.create(LoanDto.class);
        when(loanService.loanBook(userId, bookId, loanDays)).thenReturn(loanDto);

        final var result = mockMvc.perform(post("/library/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanRequest)))
                .andExpect(status().isOk()).andReturn();

        final var actual = objectMapper.readValue(result.getResponse().getContentAsString(), LoanDto.class);
        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(loanDto);
    }

    @Test
    void when_addLoan_then_return_400() throws Exception {

        final var loanRequest = new LoanRequest(10L, null, 30);

        mockMvc.perform(post("/library/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void refundLoan() throws Exception {

        final var loanId = 10L;
        final var loanDto = Instancio.create(LoanDto.class);
        when(loanService.refundBook(loanId)).thenReturn(loanDto);

        final var result = mockMvc.perform(post("/library/loans/{loanId}/refund", loanId))
                .andExpect(status().isOk())
                .andReturn();

        final var actual = objectMapper.readValue(result.getResponse().getContentAsString(), LoanDto.class);
        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(loanDto);
    }

    @Test
    void when_refundLoan_then_return_404() throws Exception {

        final var loanId = 10L;
        when(loanService.refundBook(loanId)).thenThrow(new LoanNotFoundException("loan not found"));

        mockMvc.perform(post("/library/loans/{loanId}/refund", loanId))
                .andExpect(status().isNotFound());
    }
}