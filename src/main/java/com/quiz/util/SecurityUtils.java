package com.quiz.util;

import com.quiz.constants.SecurityConstants;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static boolean isAdminAuthority(String authority) {
        if (authority == null || authority.isBlank()) {
            return false;
        }

        String normalizedAuthority = authority.startsWith(SecurityConstants.ROLE_PREFIX)
                ? authority.substring(SecurityConstants.ROLE_PREFIX.length())
                : authority;
        return SecurityConstants.QUIZ_ADMIN_ROLE.equalsIgnoreCase(normalizedAuthority);
    }
}
