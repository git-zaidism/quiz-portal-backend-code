package com.exam.service.impl;

import com.exam.config.JwtUtils;
import com.exam.dto.auth.AuthTokenResponse;
import com.exam.entities.User;
import com.exam.service.AuthService;
import com.exam.service.UserService;
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
                .anyMatch(role -> "ADMIN".equalsIgnoreCase(role) || "ROLE_ADMIN".equalsIgnoreCase(role));

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
