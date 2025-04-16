package com.library.service.impl;

import com.library.exception.UserNotFoundException;
import com.library.mapper.UserMapper;
import com.library.repository.UserRepository;
import com.library.rest.dto.PaginatedResult;
import com.library.rest.dto.UserDto;
import com.library.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static java.text.MessageFormat.format;

@Slf4j
@RequiredArgsConstructor
@Service
public class DefaultUserService implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Override
    public UserDto getUserById(Long userId) {

        return userRepository.findById(userId)
                .map(userMapper::toDto)
                .orElseThrow(() -> new UserNotFoundException(format("User with id {0} not found", userId)));
    }

    @Override
    public UserDto addUser(UserDto user) {

        final var newUser = userRepository.save(userMapper.toEntity(user));
        log.info("User with id {} has been created", newUser.getId());
        return userMapper.toDto(newUser);
    }

    @Override
    public void deleteUserById(Long userId) {

        userRepository.deleteById(userId);
        log.info("User with id {} has been deleted", userId);
    }

    @Override
    public PaginatedResult<UserDto> getAllUsers(Integer page, Integer size) {

        final var resultPage = userRepository.findAll(Pageable.ofSize(size).withPage(page));
        final var items = resultPage
                .stream()
                .map(userMapper::toDto)
                .toList();

        log.info("User list has been returned {}", items);
        return new PaginatedResult<>(items, resultPage.getTotalElements(), resultPage.getNumber(), resultPage.getSize());
    }
}
