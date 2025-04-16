package com.library.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserDto(Long id,
                      @NotBlank(message = "FirstName is mandatory")
                      String firstName,
                      @NotBlank(message = "LastName is mandatory")
                      String lastName,
                      @Email(message = "Email should be valid")
                      String email) {
}
