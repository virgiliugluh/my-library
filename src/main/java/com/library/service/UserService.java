package com.library.service;

import com.library.rest.dto.PaginatedResult;
import com.library.rest.dto.UserDto;

public interface UserService {

    UserDto getUserById(Long userId);

    UserDto addUser(UserDto user);

    void deleteUserById(Long userId);

    PaginatedResult<UserDto> getAllUsers(final Integer page, final Integer size);
}
