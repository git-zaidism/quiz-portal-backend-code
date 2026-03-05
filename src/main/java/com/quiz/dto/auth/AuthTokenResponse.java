package com.quiz.dto.auth;

public record AuthTokenResponse(
        String token,
        Long expiresInMinutes
) {
}
