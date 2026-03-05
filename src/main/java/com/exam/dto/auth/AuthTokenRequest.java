package com.exam.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record AuthTokenRequest(
        @NotBlank String username,
        @NotBlank String password
) {
}
