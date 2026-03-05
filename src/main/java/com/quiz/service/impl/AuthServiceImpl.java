package com.quiz.service.impl;

import com.quiz.config.security.JwtUtils;
import com.quiz.dto.auth.AuthTokenResponse;
import com.quiz.entities.User;
import com.quiz.service.AuthService;
import com.quiz.service.UserService;
import com.quiz.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtils jwtUtils;

    @Override
    public AuthTokenResponse generateToken(String username, String password) {
        log.info("Generating JWT token for username={}", username);
        authenticate(username, password);
        User user = this.userService.getUserByUsername(username);
        String token = this.jwtUtils.generateToken(user);
        log.debug("JWT token generated successfully for username={}", username);
        return new AuthTokenResponse(token, this.jwtUtils.getTokenValidityMinutes());
    }

    @Override
    public AuthTokenResponse generateAdminToken(String username, String password) {
        log.info("Generating admin JWT token for username={}", username);
        authenticate(username, password);
        User user = this.userService.getUserByUsername(username);

        boolean isAdmin = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(SecurityUtils::isAdminAuthority);

        if (!isAdmin) {
            log.warn("Admin token request denied for non-admin username={}", username);
            throw new AccessDeniedException("Only admin user can generate admin token.");
        }

        String token = this.jwtUtils.generateToken(user);
        log.debug("Admin JWT token generated successfully for username={}", username);
        return new AuthTokenResponse(token, this.jwtUtils.getTokenValidityMinutes());
    }

    private void authenticate(String username, String password) {
        try {
            this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            log.debug("Authentication succeeded for username={}", username);
        } catch (AuthenticationException exception) {
            log.warn("Authentication failed for username={}", username);
            throw exception;
        }
    }
}
