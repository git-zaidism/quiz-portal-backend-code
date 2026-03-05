package com.quiz.config.security;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtUtils Tests")
class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
    }

    @Test
    @DisplayName("Should generate valid JWT token with correct subject and expiration")
    void testGenerateToken() {
        // Arrange
        String username = "testuser";
        UserDetails user = new User(username, "password", new ArrayList<>());

        // Act
        String token = jwtUtils.generateToken(user);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals(username, jwtUtils.extractUsername(token));
    }

    @Test
    @DisplayName("Should extract username from valid token")
    void testExtractUsername() {
        // Arrange
        String username = "testuser";
        UserDetails user = new User(username, "password", new ArrayList<>());
        String token = jwtUtils.generateToken(user);

        // Act
        String extractedUsername = jwtUtils.extractUsername(token);

        // Assert
        assertEquals(username, extractedUsername);
    }

    @Test
    @DisplayName("Should extract expiration date from token")
    void testExtractExpiration() {
        // Arrange
        UserDetails user = new User("testuser", "password", new ArrayList<>());
        String token = jwtUtils.generateToken(user);

        // Act
        Date expiration = jwtUtils.extractExpiration(token);

        // Assert
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    @DisplayName("Should validate token with correct username and non-expired token")
    void testValidateToken_Success() {
        // Arrange
        String username = "testuser";
        UserDetails user = new User(username, "password", new ArrayList<>());
        String token = jwtUtils.generateToken(user);

        // Act
        boolean isValid = jwtUtils.validateToken(token, user);

        // Assert
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Should reject token with different username")
    void testValidateToken_DifferentUsername() {
        // Arrange
        UserDetails user1 = new User("user1", "password", new ArrayList<>());
        UserDetails user2 = new User("user2", "password", new ArrayList<>());
        String token = jwtUtils.generateToken(user1);

        // Act
        boolean isValid = jwtUtils.validateToken(token, user2);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should reject expired token")
    void testValidateToken_ExpiredToken() {
        // Arrange
        String username = "testuser";
        UserDetails user = new User(username, "password", new ArrayList<>());
        
        // Create a token that will be expired by the time we validate it
        // We create it with very short expiration (1 millisecond)
        long now = System.currentTimeMillis();
        String expiredToken = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + 1)) // Expires in 1ms
                .signWith(SignatureAlgorithm.HS256, "examportal")
                .compact();

        // Sleep to ensure token is actually expired
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Act & Assert
        // validateToken throws ExpiredJwtException when token is expired
        assertThrows(io.jsonwebtoken.ExpiredJwtException.class, 
            () -> jwtUtils.validateToken(expiredToken, user));
    }

    @Test
    @DisplayName("Should return correct token validity in minutes")
    void testGetTokenValidityMinutes() {
        // Act
        long validity = jwtUtils.getTokenValidityMinutes();

        // Assert
        assertEquals(600L, validity);
    }

    @Test
    @DisplayName("Should extract custom claim from token")
    void testExtractClaim() {
        // Arrange
        String username = "testuser";
        UserDetails user = new User(username, "password", new ArrayList<>());
        String token = jwtUtils.generateToken(user);

        // Act
        String extractedSubject = jwtUtils.extractClaim(token, Claims::getSubject);

        // Assert
        assertEquals(username, extractedSubject);
    }

    @Test
    @DisplayName("Should throw exception for invalid token format")
    void testExtractUsername_InvalidToken() {
        // Arrange
        String invalidToken = "invalid.token.format";

        // Act & Assert
        assertThrows(Exception.class, () -> jwtUtils.extractUsername(invalidToken));
    }

    @Test
    @DisplayName("Should handle null token gracefully")
    void testExtractUsername_NullToken() {
        // Act & Assert
        assertThrows(Exception.class, () -> jwtUtils.extractUsername(null));
    }

    @Test
    @DisplayName("Token should contain valid expiration date in future")
    void testTokenExpiration_InFuture() {
        // Arrange
        UserDetails user = new User("testuser", "password", new ArrayList<>());
        String token = jwtUtils.generateToken(user);

        // Act
        Date expiration = jwtUtils.extractExpiration(token);
        Date now = new Date();

        // Assert
        assertTrue(expiration.after(now), "Token expiration should be in the future");
    }

    @Test
    @DisplayName("Different tokens should have different signatures")
    void testGenerateToken_DifferentTokens() {
        // Arrange
        UserDetails user1 = new User("user1", "password", new ArrayList<>());
        UserDetails user2 = new User("user2", "password", new ArrayList<>());

        // Act
        String token1 = jwtUtils.generateToken(user1);
        String token2 = jwtUtils.generateToken(user2);

        // Assert
        assertNotEquals(token1, token2);
    }
}
