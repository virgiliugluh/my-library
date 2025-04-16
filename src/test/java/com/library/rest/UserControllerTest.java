package com.library.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.exception.UserNotFoundException;
import com.library.rest.dto.PaginatedResult;
import com.library.rest.dto.UserDto;
import com.library.service.impl.DefaultUserService;
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
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DefaultUserService userService;

    @Test
    void getUserById() throws Exception {

        final var userDto = Instancio.create(UserDto.class);
        final var userId = userDto.id();
        when(userService.getUserById(userDto.id())).thenReturn(userDto);

        final var result = mockMvc.perform(get("/library/users/{userId}", userId))
                .andExpect(status().isOk())
                .andReturn();

        final var actual = objectMapper.readValue(result.getResponse().getContentAsString(), UserDto.class);
        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(userDto);
    }

    @Test
    void when_getUserById_then_return_404() throws Exception {

        final var userId = 10L;
        when(userService.getUserById(userId)).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(get("/library/users/{userId}", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void addUser() throws Exception {

        final var userDto = new UserDto(10L, "firstName", "lastName", "email@email.com");
        final var userDtoResponse = new UserDto(100L, "firstName2", "lastName2", "email2@email.com");
        when(userService.addUser(userDto)).thenReturn(userDtoResponse);

        final var result = mockMvc.perform(post("/library/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk()).andReturn();

        final var actual = objectMapper.readValue(result.getResponse().getContentAsString(), UserDto.class);
        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(userDtoResponse);
    }

    @Test
    void when_addUser_then_return_400() throws Exception {

        final var userDto = new UserDto(10L, null, "lastName", "email@email.com");

        mockMvc.perform(post("/library/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteUserById() throws Exception {

        final var userId = 10L;
        doNothing().when(userService).deleteUserById(userId);

        mockMvc.perform(delete("/library/users/{userId}", userId))
                .andExpect(status().isOk());
    }

    @Test
    void getAllUsers() throws Exception {

        final var pageNumber = 1;
        final var pageSize = 10;
        final var paginatedResult = Instancio.create(new TypeToken<PaginatedResult<UserDto>>() {
        });
        when(userService.getAllUsers(pageNumber, pageSize)).thenReturn(paginatedResult);

        final var result = mockMvc.perform(get("/library/users")
                        .param("page", String.valueOf(pageNumber))
                        .param("size", String.valueOf(pageSize)))
                .andExpect(status().isOk())
                .andReturn();

        final var actual = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<PaginatedResult<UserDto>>() {
        });
        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(paginatedResult);
    }
}