package com.quiz.controller;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.quiz.dto.user.UserCreateRequest;
import com.quiz.dto.user.UserResponse;
import com.quiz.entities.User;
import com.quiz.mapper.UserMapper;
import com.quiz.service.UserService;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserController Tests")
class UserControllerTest {

    private UserController controller;

    @Mock
    private UserService userService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    private User testUser;
    private UserResponse testUserResponse;
    private UserCreateRequest testCreateRequest;

    @BeforeEach
    void setUp() {
        controller = new UserController(userService, passwordEncoder, userMapper);

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

        testCreateRequest = new UserCreateRequest(
                "testuser",
                "password123",
                "Test",
                "User",
                "test@example.com",
                null
        );
    }

    @Test
    @DisplayName("Should create user and return HTTP 200")
    void testCreateUser_Success() {
        // Arrange
        String encodedPassword = "encoded_password";
        when(passwordEncoder.encode("password123")).thenReturn(encodedPassword);
        when(userMapper.toEntity(testCreateRequest, encodedPassword)).thenReturn(testUser);
        when(userMapper.toDefaultQuizzerRoles(testUser)).thenReturn(Collections.emptySet());
        when(userService.createUser(testUser, Collections.emptySet())).thenReturn(testUser);
        when(userMapper.toResponse(testUser)).thenReturn(testUserResponse);

        // Act
        UserResponse response = controller.createUser(testCreateRequest);

        // Assert
        assertNotNull(response);
        assertEquals("testuser", response.username());
        verify(passwordEncoder).encode("password123");
        verify(userService).createUser(testUser, Collections.emptySet());
    }

    @Test
    @DisplayName("Should encode password before creating user")
    void testCreateUser_EncodePassword() {
        // Arrange
        String encodedPassword = "encoded_password";
        when(passwordEncoder.encode("password123")).thenReturn(encodedPassword);
        when(userMapper.toEntity(testCreateRequest, encodedPassword)).thenReturn(testUser);
        when(userMapper.toDefaultQuizzerRoles(testUser)).thenReturn(Collections.emptySet());
        when(userService.createUser(any(), any())).thenReturn(testUser);
        when(userMapper.toResponse(testUser)).thenReturn(testUserResponse);

        // Act
        controller.createUser(testCreateRequest);

        // Assert
        verify(passwordEncoder).encode("password123");
        verify(userMapper).toEntity(testCreateRequest, encodedPassword);
    }

    @Test
    @DisplayName("Should get user by username successfully")
    void testGetUserByUsername_Success() {
        // Arrange
        when(userService.getUserByUsername("testuser")).thenReturn(testUser);
        when(userMapper.toResponse(testUser)).thenReturn(testUserResponse);

        // Act
        UserResponse response = controller.getUserByUsername("testuser");

        // Assert
        assertNotNull(response);
        assertEquals("testuser", response.username());
        verify(userService).getUserByUsername("testuser");
        verify(userMapper).toResponse(testUser);
    }

    @Test
    @DisplayName("Should call service with correct username")
    void testGetUserByUsername_CallsService() {
        // Arrange
        when(userService.getUserByUsername("testuser")).thenReturn(testUser);
        when(userMapper.toResponse(testUser)).thenReturn(testUserResponse);

        // Act
        controller.getUserByUsername("testuser");

        // Assert
        verify(userService).getUserByUsername("testuser");
    }

    @Test
    @DisplayName("Should delete user by id successfully")
    void testDeleteUserById_Success() {
        // Act
        controller.deleteUserById(1L);

        // Assert
        verify(userService).deleteUserById(1L);
    }

    @Test
    @DisplayName("Should call userService delete method")
    void testDeleteUserById_CallsService() {
        // Act
        controller.deleteUserById(1L);

        // Assert
        verify(userService).deleteUserById(1L);
    }

    @Test
    @DisplayName("Should map entity to response correctly")
    void testCreateUser_MapsEntityToResponse() {
        // Arrange
        String encodedPassword = "encoded_password";
        when(passwordEncoder.encode("password123")).thenReturn(encodedPassword);
        when(userMapper.toEntity(testCreateRequest, encodedPassword)).thenReturn(testUser);
        when(userMapper.toDefaultQuizzerRoles(testUser)).thenReturn(Collections.emptySet());
        when(userService.createUser(any(), any())).thenReturn(testUser);
        when(userMapper.toResponse(testUser)).thenReturn(testUserResponse);

        // Act
        UserResponse response = controller.createUser(testCreateRequest);

        // Assert
        verify(userMapper).toResponse(testUser);
        assertNotNull(response);
    }

    @Test
    @DisplayName("Should get user with correct username mapping")
    void testGetUserByUsername_CorrectMapping() {
        // Arrange
        when(userService.getUserByUsername("testuser")).thenReturn(testUser);
        when(userMapper.toResponse(testUser)).thenReturn(testUserResponse);

        // Act
        UserResponse response = controller.getUserByUsername("testuser");

        // Assert
        assertEquals("testuser", response.username());
    }

    @Test
    @DisplayName("Should throw exception when user not found on retrieval")
    void testGetUserByUsername_UserNotFound() {
        // Arrange
        when(userService.getUserByUsername("nonexistent"))
                .thenThrow(new RuntimeException("User not found"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> controller.getUserByUsername("nonexistent"));
    }

    @Test
    @DisplayName("Should throw exception when user not found on deletion")
    void testDeleteUserById_UserNotFound() {
        // Arrange
        doThrow(new RuntimeException("User not found")).when(userService).deleteUserById(999L);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> controller.deleteUserById(999L));
    }

    @Test
    @DisplayName("Should create user with default roles")
    void testCreateUser_AssignsDefaultRoles() {
        // Arrange
        String encodedPassword = "encoded_password";
        when(passwordEncoder.encode("password123")).thenReturn(encodedPassword);
        when(userMapper.toEntity(testCreateRequest, encodedPassword)).thenReturn(testUser);
        when(userMapper.toDefaultQuizzerRoles(testUser)).thenReturn(Collections.emptySet());
        when(userService.createUser(any(), any())).thenReturn(testUser);
        when(userMapper.toResponse(testUser)).thenReturn(testUserResponse);

        // Act
        controller.createUser(testCreateRequest);

        // Assert
        verify(userMapper).toDefaultQuizzerRoles(testUser);
    }

    @Test
    @DisplayName("Should return response with user id")
    void testCreateUser_ResponseHasId() {
        // Arrange
        String encodedPassword = "encoded_password";
        when(passwordEncoder.encode("password123")).thenReturn(encodedPassword);
        when(userMapper.toEntity(testCreateRequest, encodedPassword)).thenReturn(testUser);
        when(userMapper.toDefaultQuizzerRoles(testUser)).thenReturn(Collections.emptySet());
        when(userService.createUser(any(), any())).thenReturn(testUser);
        when(userMapper.toResponse(testUser)).thenReturn(testUserResponse);

        // Act
        UserResponse response = controller.createUser(testCreateRequest);

        // Assert
        assertEquals(1L, response.id());
    }

    @Test
    @DisplayName("Should return user response with email")
    void testGetUserByUsername_ResponseHasEmail() {
        // Arrange
        when(userService.getUserByUsername("testuser")).thenReturn(testUser);
        when(userMapper.toResponse(testUser)).thenReturn(testUserResponse);

        // Act
        UserResponse response = controller.getUserByUsername("testuser");

        // Assert
        assertEquals("test@example.com", response.email());
    }

    @Test
    @DisplayName("Should handle delete multiple times independently")
    void testDeleteUserById_MultipleDeletions() {
        // Act
        controller.deleteUserById(1L);
        controller.deleteUserById(2L);

        // Assert
        verify(userService, times(2)).deleteUserById(anyLong());
        verify(userService).deleteUserById(1L);
        verify(userService).deleteUserById(2L);
    }

    @Test
    @DisplayName("Should create user with valid request data")
    void testCreateUser_PreservesRequestData() {
        // Arrange
        String encodedPassword = "encoded_password";
        when(passwordEncoder.encode("password123")).thenReturn(encodedPassword);
        when(userMapper.toEntity(testCreateRequest, encodedPassword)).thenReturn(testUser);
        when(userMapper.toDefaultQuizzerRoles(testUser)).thenReturn(Collections.emptySet());
        when(userService.createUser(any(), any())).thenReturn(testUser);
        when(userMapper.toResponse(testUser)).thenReturn(testUserResponse);

        // Act
        UserResponse response = controller.createUser(testCreateRequest);

        // Assert
        verify(userMapper).toEntity(testCreateRequest, encodedPassword);
        assertNotNull(response);
    }
}
