package com.library.service.impl;

import com.library.exception.UserNotFoundException;
import com.library.mapper.UserMapper;
import com.library.mapper.UserMapperImpl;
import com.library.repository.UserRepository;
import com.library.repository.entity.UserEntity;
import com.library.rest.dto.UserDto;
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
class DefaultUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserMapper userMapper = new UserMapperImpl();

    @InjectMocks
    private DefaultUserService defaultUserService;

    @Test
    void getUserById() {

        final var userId = 1L;
        final var userEntity = Instancio.create(UserEntity.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));

        final var userDto = defaultUserService.getUserById(userId);

        assertThat(userDto.id()).isEqualTo(userEntity.getId());
        assertThat(userDto.firstName()).isEqualTo(userEntity.getFirstName());
        assertThat(userDto.lastName()).isEqualTo(userEntity.getLastName());
        assertThat(userDto.email()).isEqualTo(userEntity.getEmail());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void when_getUserById_then_throw_exception() {

        final var userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> defaultUserService.getUserById(userId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(format("User with id {0} not found", userId));

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void addUser() {

        final var userDto = Instancio.create(UserDto.class);
        final var userEntity = userMapper.toEntity(userDto);

        when(userRepository.save(userEntity)).thenReturn(userEntity);

        final var savedUserDto = defaultUserService.addUser(userDto);

        assertThat(savedUserDto.id()).isEqualTo(userEntity.getId());
        assertThat(savedUserDto.firstName()).isEqualTo(userEntity.getFirstName());
        assertThat(savedUserDto.lastName()).isEqualTo(userEntity.getLastName());
        assertThat(savedUserDto.email()).isEqualTo(userEntity.getEmail());

        verify(userRepository, times(1)).save(userEntity);
    }

    @Test
    void deleteUserById() {

        final var userId = 1L;

        doNothing().when(userRepository).deleteById(userId);

        defaultUserService.deleteUserById(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void getAllUsers() {

        final var pageable = Pageable.ofSize(100).withPage(1);
        final var userEntity = Instancio.create(UserEntity.class);

        when(userRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(userEntity), pageable, 1L));

        final var paginatedResult = defaultUserService.getAllUsers(pageable.getPageNumber(), pageable.getPageSize());

        assertThat(paginatedResult.results().size()).isEqualTo(1);
        assertThat(paginatedResult.pageNumber()).isEqualTo(pageable.getPageNumber());
        assertThat(paginatedResult.pageSize()).isEqualTo(pageable.getPageSize());

        final var userDto = paginatedResult.results().getFirst();

        assertThat(userDto.id()).isEqualTo(userEntity.getId());
        assertThat(userDto.firstName()).isEqualTo(userEntity.getFirstName());
        assertThat(userDto.lastName()).isEqualTo(userEntity.getLastName());
        assertThat(userDto.email()).isEqualTo(userEntity.getEmail());

        verify(userRepository, times(1)).findAll(pageable);
    }
}