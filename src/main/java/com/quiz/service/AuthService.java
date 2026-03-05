package com.quiz.service;

import com.quiz.dto.auth.AuthTokenResponse;

public interface AuthService {

    AuthTokenResponse generateToken(String username, String password);

    AuthTokenResponse generateAdminToken(String username, String password);
}
