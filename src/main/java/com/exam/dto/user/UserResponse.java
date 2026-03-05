package com.exam.dto.user;

import java.util.Set;

public record UserResponse(
        Long id,
        String username,
        String firstName,
        String lastName,
        String email,
        String phone,
        boolean enabled,
        String profile,
        Set<String> roles
) {
}
