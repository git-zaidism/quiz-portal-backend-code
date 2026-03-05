package com.quiz.controller;

import java.security.Principal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.quiz.dto.auth.AuthTokenRequest;
import com.quiz.dto.auth.AuthTokenResponse;
import com.quiz.dto.user.UserResponse;
import com.quiz.entities.User;
import com.quiz.mapper.UserMapper;
import com.quiz.service.AuthService;
import com.quiz.service.UserService;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthenticateController Tests")
class AuthenticateControllerTest {

    private AuthenticateController controller;

    @Mock
    private AuthService authService;

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private Principal principal;

    private User testUser;
    private UserResponse testUserResponse;

    @BeforeEach
    void setUp() {
        controller = new AuthenticateController(authService, userService, userMapper);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setEnabled(true);

        testUserResponse = new UserResponse(
                1L,
                "testuser",
                "Test",
                "User",
                "test@example.com",
                null,
                true,
                null,
                Set.of("QUIZZER"),
                Set.of()
        );
    }

    @Test
    @DisplayName("Should generate token and return with HTTP 200")
    void testGenerateToken_Success() {
        // Arrange
        AuthTokenRequest request = new AuthTokenRequest("testuser", "password123");
        AuthTokenResponse expectedResponse = new AuthTokenResponse("jwt.token", 600L);

        when(authService.generateToken("testuser", "password123"))
                .thenReturn(expectedResponse);

        // Act
        ResponseEntity<AuthTokenResponse> response = controller.generateToken(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("jwt.token", response.getBody().token());
        assertEquals(600L, response.getBody().expiresInMinutes());
        verify(authService).generateToken("testuser", "password123");
    }

    @Test
    @DisplayName("Should call authService with correct username and password")
    void testGenerateToken_CallsAuthService() {
        // Arrange
        AuthTokenRequest request = new AuthTokenRequest("testuser", "password");
        when(authService.generateToken(anyString(), anyString()))
                .thenReturn(new AuthTokenResponse("token", 600L));

        // Act
        controller.generateToken(request);

        // Assert
        verify(authService).generateToken("testuser", "password");
    }

    @Test
    @DisplayName("Should generate admin token and return with HTTP 200")
    void testGenerateAdminToken_Success() {
        // Arrange
        AuthTokenRequest request = new AuthTokenRequest("admin", "password123");
        AuthTokenResponse expectedResponse = new AuthTokenResponse("admin.jwt.token", 600L);

        when(authService.generateAdminToken("admin", "password123"))
                .thenReturn(expectedResponse);

        // Act
        ResponseEntity<AuthTokenResponse> response = controller.generateAdminToken(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("admin.jwt.token", response.getBody().token());
        verify(authService).generateAdminToken("admin", "password123");
    }

    @Test
    @DisplayName("Should return current authenticated user")
    void testGetCurrentAuthenticatedUser_Success() {
        // Arrange
        when(principal.getName()).thenReturn("testuser");
        when(userService.getUserByUsername("testuser")).thenReturn(testUser);
        when(userMapper.toResponse(testUser)).thenReturn(testUserResponse);

        // Act
        UserResponse response = controller.getCurrentAuthenticatedUser(principal);

        // Assert
        assertNotNull(response);
        assertEquals("testuser", response.username());
        assertEquals("Test", response.firstName());
        verify(userService).getUserByUsername("testuser");
        verify(userMapper).toResponse(testUser);
    }

    @Test
    @DisplayName("Should call userService to get user by username")
    void testGetCurrentAuthenticatedUser_CallsService() {
        // Arrange
        when(principal.getName()).thenReturn("testuser");
        when(userService.getUserByUsername("testuser")).thenReturn(testUser);
        when(userMapper.toResponse(testUser)).thenReturn(testUserResponse);

        // Act
        controller.getCurrentAuthenticatedUser(principal);

        // Assert
        verify(userService).getUserByUsername("testuser");
    }

    @Test
    @DisplayName("Should pass token response correctly")
    void testGenerateToken_ResponseBody() {
        // Arrange
        AuthTokenRequest request = new AuthTokenRequest("user", "pass");
        String expectedToken = "some.jwt.token.value";
        long expectedValidity = 600L;
        AuthTokenResponse expectedResponse = new AuthTokenResponse(expectedToken, expectedValidity);

        when(authService.generateToken("user", "pass"))
                .thenReturn(expectedResponse);

        // Act
        ResponseEntity<AuthTokenResponse> response = controller.generateToken(request);

        // Assert
        assertEquals(expectedToken, response.getBody().token());
        assertEquals(expectedValidity, response.getBody().expiresInMinutes());
    }

    @Test
    @DisplayName("Should handle null request gracefully")
    void testGenerateToken_NullRequest() {
        // Act & Assert
        assertThrows(Exception.class, () -> controller.generateToken(null));
    }

    @Test
    @DisplayName("Should map user entity to response correctly")
    void testGetCurrentAuthenticatedUser_MapsCorrectly() {
        // Arrange
        when(principal.getName()).thenReturn("testuser");
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        when(userService.getUserByUsername("testuser")).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(testUserResponse);

        // Act
        UserResponse response = controller.getCurrentAuthenticatedUser(principal);

        // Assert
        verify(userMapper).toResponse(user);
        assertNotNull(response);
    }

    @Test
    @DisplayName("Should throw exception when authService throws exception")
    void testGenerateToken_AuthServiceThrowsException() {
        // Arrange
        AuthTokenRequest request = new AuthTokenRequest("user", "wrongpass");
        when(authService.generateToken("user", "wrongpass"))
                .thenThrow(new RuntimeException("Invalid credentials"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> controller.generateToken(request));
    }

    @Test
    @DisplayName("Should throw exception when userService throws exception")
    void testGetCurrentAuthenticatedUser_UserServiceThrowsException() {
        // Arrange
        when(principal.getName()).thenReturn("nonexistent");
        when(userService.getUserByUsername("nonexistent"))
                .thenThrow(new RuntimeException("User not found"));

        // Act & Assert
        assertThrows(RuntimeException.class, 
                () -> controller.getCurrentAuthenticatedUser(principal));
    }

    @Test
    @DisplayName("Should generate token with valid response entity")
    void testGenerateToken_ValidResponseEntity() {
        // Arrange
        AuthTokenRequest request = new AuthTokenRequest("testuser", "password");
        AuthTokenResponse response = new AuthTokenResponse("token", 600L);
        when(authService.generateToken("testuser", "password")).thenReturn(response);

        // Act
        ResponseEntity<AuthTokenResponse> result = controller.generateToken(request);

        // Assert
        assertNotNull(result);
        assertTrue(result.hasBody());
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    @DisplayName("Should get current user with principal name")
    void testGetCurrentAuthenticatedUser_WithPrincipal() {
        // Arrange
        String expectedUsername = "testuser";
        when(principal.getName()).thenReturn(expectedUsername);
        when(userService.getUserByUsername(expectedUsername)).thenReturn(testUser);
        when(userMapper.toResponse(testUser)).thenReturn(testUserResponse);

        // Act
        UserResponse response = controller.getCurrentAuthenticatedUser(principal);

        // Assert
        verify(userService).getUserByUsername(expectedUsername);
        assertNotNull(response);
    }
}
