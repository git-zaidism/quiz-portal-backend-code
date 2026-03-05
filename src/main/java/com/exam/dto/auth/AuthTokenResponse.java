package com.exam.dto.auth;

public record AuthTokenResponse(
        String token,
        Long expiresInMinutes
) {
}
