package com.quiz.service.impl;

import com.quiz.config.JwtUtils;
import com.quiz.dto.auth.AuthTokenResponse;
import com.quiz.entities.User;
import com.quiz.service.AuthService;
import com.quiz.service.UserService;
import com.quiz.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtils jwtUtils;

    @Override
    public AuthTokenResponse generateToken(String username, String password) {
        authenticate(username, password);
        User user = this.userService.getUserByUsername(username);
        String token = this.jwtUtils.generateToken(user);
        return new AuthTokenResponse(token, this.jwtUtils.getTokenValidityMinutes());
    }

    @Override
    public AuthTokenResponse generateAdminToken(String username, String password) {
        authenticate(username, password);
        User user = this.userService.getUserByUsername(username);

        boolean isAdmin = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(SecurityUtils::isAdminAuthority);

        if (!isAdmin) {
            throw new AccessDeniedException("Only admin user can generate admin token.");
        }

        String token = this.jwtUtils.generateToken(user);
        return new AuthTokenResponse(token, this.jwtUtils.getTokenValidityMinutes());
    }

    private void authenticate(String username, String password) {
        this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }
}
