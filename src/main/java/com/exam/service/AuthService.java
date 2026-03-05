package com.exam.service;

import com.exam.dto.auth.AuthTokenResponse;

public interface AuthService {

    AuthTokenResponse generateToken(String username, String password);

    AuthTokenResponse generateAdminToken(String username, String password);
}
