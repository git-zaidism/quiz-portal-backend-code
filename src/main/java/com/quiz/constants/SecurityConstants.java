package com.quiz.constants;

public final class SecurityConstants {

    private SecurityConstants() {
    }

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    public static final String ROLE_PREFIX = "ROLE_";
    public static final String QUIZ_ADMIN_ROLE = "QUIZ_ADMIN";

    public static final String ANONYMOUS_USERNAME = "anonymousUser";
    public static final String SYSTEM_AUDITOR = "SYSTEM";
}
