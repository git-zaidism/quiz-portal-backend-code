package com.exam.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserCreateRequest(
        @NotBlank String username,
        @NotBlank String password,
        @NotBlank String firstName,
        String lastName,
        @Email @NotBlank String email,
        String phone
) {
}
