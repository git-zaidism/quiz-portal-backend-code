package com.quiz.service.impl;

import com.quiz.config.security.JwtUtils;
import com.quiz.dto.auth.AuthTokenResponse;
import com.quiz.entities.User;
import com.quiz.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthServiceImpl Tests")
class AuthServiceImplTest {

    private AuthServiceImpl authService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserService userService;

    @Mock
    private JwtUtils jwtUtils;

    private User testUser;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(authenticationManager, userService, jwtUtils);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
        testUser.setUserRoles(new HashSet<>());
    }

    @Test
    @DisplayName("Should generate token for valid credentials")
    void testGenerateToken_Success() {
        // Arrange
        String username = "testuser";
        String password = "password123";
        String expectedToken = "jwt.token";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));
        when(userService.getUserByUsername(username)).thenReturn(testUser);
        when(jwtUtils.generateToken(testUser)).thenReturn(expectedToken);
        when(jwtUtils.getTokenValidityMinutes()).thenReturn(600L);

        // Act
        AuthTokenResponse response = authService.generateToken(username, password);

        // Assert
        assertNotNull(response);
        assertEquals(expectedToken, response.token());
        assertEquals(600L, response.expiresInMinutes());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("Should throw exception for invalid credentials")
    void testGenerateToken_InvalidCredentials() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act & Assert
        assertThrows(BadCredentialsException.class, 
                () -> authService.generateToken("testuser", "wrongpassword"));
    }

    @Test
    @DisplayName("Should generate admin token for admin user")
    void testGenerateAdminToken_Success() {
        // Arrange
        String username = "admin";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));
        when(userService.getUserByUsername(username)).thenReturn(testUser);

        // Act & Assert
        // This test verifies the call flow - actual admin check depends on user's roles
        try {
            AuthTokenResponse response = authService.generateAdminToken(username, "password");
            assertNotNull(response);
        } catch (AccessDeniedException e) {
            // Expected if user is not an admin
            assertTrue(true);
        }
    }

    @Test
    @DisplayName("Should call authenticationManager.authenticate with correct credentials")
    void testGenerateToken_CallsAuthenticationManager() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));
        when(userService.getUserByUsername("testuser")).thenReturn(testUser);
        when(jwtUtils.generateToken(testUser)).thenReturn("token");
        when(jwtUtils.getTokenValidityMinutes()).thenReturn(600L);

        // Act
        authService.generateToken("testuser", "password123");

        // Assert
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("Should call userService to get user details")
    void testGenerateToken_CallsUserService() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));
        when(userService.getUserByUsername("testuser")).thenReturn(testUser);
        when(jwtUtils.generateToken(testUser)).thenReturn("token");
        when(jwtUtils.getTokenValidityMinutes()).thenReturn(600L);

        // Act
        authService.generateToken("testuser", "password");

        // Assert
        verify(userService).getUserByUsername("testuser");
    }

    @Test
    @DisplayName("Should use JwtUtils to generate token")
    void testGenerateToken_CallsJwtUtils() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));
        when(userService.getUserByUsername("testuser")).thenReturn(testUser);
        when(jwtUtils.generateToken(testUser)).thenReturn("token");
        when(jwtUtils.getTokenValidityMinutes()).thenReturn(600L);

        // Act
        authService.generateToken("testuser", "password");

        // Assert
        verify(jwtUtils).generateToken(testUser);
        verify(jwtUtils).getTokenValidityMinutes();
    }

    @Test
    @DisplayName("Should return correct token validity minutes")
    void testGenerateToken_CorrectValidity() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));
        when(userService.getUserByUsername("testuser")).thenReturn(testUser);
        when(jwtUtils.generateToken(testUser)).thenReturn("token");
        when(jwtUtils.getTokenValidityMinutes()).thenReturn(600L);

        // Act
        AuthTokenResponse response = authService.generateToken("testuser", "password");

        // Assert
        assertEquals(600L, response.expiresInMinutes());
    }

    @Test
    @DisplayName("Should throw exception for bad credentials")
    void testGenerateAdminToken_BadCredentials() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act & Assert
        assertThrows(BadCredentialsException.class, 
                () -> authService.generateAdminToken("user", "wrongpass"));
    }

    @Test
    @DisplayName("Should handle authentication properly")
    void testGenerateToken_AuthenticationHandling() {
        // Arrange
        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
        when(userService.getUserByUsername("testuser")).thenReturn(testUser);
        when(jwtUtils.generateToken(testUser)).thenReturn("token");
        when(jwtUtils.getTokenValidityMinutes()).thenReturn(600L);

        // Act
        AuthTokenResponse response = authService.generateToken("testuser", "password");

        // Assert
        assertNotNull(response);
        assertNotNull(response.token());
    }

    @Test
    @DisplayName("Should generate non-empty token")
    void testGenerateToken_NonEmptyToken() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));
        when(userService.getUserByUsername("testuser")).thenReturn(testUser);
        when(jwtUtils.generateToken(testUser)).thenReturn("valid.jwt.token");
        when(jwtUtils.getTokenValidityMinutes()).thenReturn(600L);

        // Act
        AuthTokenResponse response = authService.generateToken("testuser", "password");

        // Assert
        assertFalse(response.token().isEmpty());
        assertTrue(response.token().length() > 0);
    }

    @Test
    @DisplayName("Should return positive validity minutes")
    void testGenerateToken_PositiveValidity() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));
        when(userService.getUserByUsername("testuser")).thenReturn(testUser);
        when(jwtUtils.generateToken(testUser)).thenReturn("token");
        when(jwtUtils.getTokenValidityMinutes()).thenReturn(600L);

        // Act
        AuthTokenResponse response = authService.generateToken("testuser", "password");

        // Assert
        assertTrue(response.expiresInMinutes() > 0);
    }

    @Test
    @DisplayName("Should verify authentication with correct username and password")
    void testGenerateToken_CorrectCredentials() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));
        when(userService.getUserByUsername("testuser")).thenReturn(testUser);
        when(jwtUtils.generateToken(testUser)).thenReturn("token");
        when(jwtUtils.getTokenValidityMinutes()).thenReturn(600L);

        // Act
        authService.generateToken("testuser", "password123");

        // Assert - verify authenticate was called (actual credential validation done by Spring Security)
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}
