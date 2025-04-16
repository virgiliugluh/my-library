package com.library.rest;

import com.library.rest.dto.PaginatedResult;
import com.library.rest.dto.UserDto;
import com.library.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/library/users")
public class UserController {

    private final UserService userService;


    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {

        return userService.getUserById(userId);
    }

    @PostMapping
    public UserDto addUser(@Valid @RequestBody final UserDto user) {

        return userService.addUser(user);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable Long userId) {

        userService.deleteUserById(userId);
    }

    @GetMapping
    public PaginatedResult<UserDto> getAllUsers(@RequestParam Integer page, @RequestParam Integer size) {

        return userService.getAllUsers(page, size);
    }
}
