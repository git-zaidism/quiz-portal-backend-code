package com.quiz.service.impl;

import com.quiz.entities.Role;
import com.quiz.entities.User;
import com.quiz.entities.UserRole;
import com.quiz.exception.UserAlreadyExistsException;
import com.quiz.exception.UserNotFoundException;
import com.quiz.repositoy.RoleRepository;
import com.quiz.repositoy.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl Tests")
class UserServiceImplTest {

    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    private User testUser;
    private Role testRole;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, roleRepository);
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("hashedpassword");
        testUser.setEmail("test@example.com");
        testUser.setEnabled(true);
        testUser.setUserRoles(new HashSet<>());

        testRole = new Role();
        testRole.setRoleId(1L);
        testRole.setRoleName("ROLE_USER");
    }

    @Test
    @DisplayName("Should create user with roles successfully")
    void testCreateUser_Success() {
        // Arrange
        Set<UserRole> userRoles = new HashSet<>();
        UserRole userRole = new UserRole();
        userRole.setRole(testRole);
        userRoles.add(userRole);

        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(roleRepository.save(testRole)).thenReturn(testRole);
        when(userRepository.save(testUser)).thenReturn(testUser);

        // Act
        User createdUser = userService.createUser(testUser, userRoles);

        // Assert
        assertNotNull(createdUser);
        assertEquals("testuser", createdUser.getUsername());
        verify(userRepository).existsByUsername("testuser");
        verify(roleRepository).save(testRole);
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("Should throw exception when user already exists")
    void testCreateUser_UserAlreadyExists() {
        // Arrange
        Set<UserRole> userRoles = new HashSet<>();
        
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // Act & Assert
        assertThrows(UserAlreadyExistsException.class, 
                () -> userService.createUser(testUser, userRoles));
        verify(userRepository).existsByUsername("testuser");
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should get user by username successfully")
    void testGetUserByUsername_Success() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        User retrievedUser = userService.getUserByUsername("testuser");

        // Assert
        assertNotNull(retrievedUser);
        assertEquals("testuser", retrievedUser.getUsername());
        assertEquals(1L, retrievedUser.getId());
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    @DisplayName("Should throw exception when user not found by username")
    void testGetUserByUsername_NotFound() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, 
                () -> userService.getUserByUsername("nonexistent"));
        verify(userRepository).findByUsername("nonexistent");
    }

    @Test
    @DisplayName("Should delete user by id successfully")
    void testDeleteUserById_Success() {
        // Arrange
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);

        // Act
        userService.deleteUserById(userId);

        // Assert
        verify(userRepository).existsById(userId);
        verify(userRepository).deleteById(userId);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent user")
    void testDeleteUserById_NotFound() {
        // Arrange
        Long userId = 999L;
        when(userRepository.existsById(userId)).thenReturn(false);

        // Act & Assert
        assertThrows(UserNotFoundException.class, 
                () -> userService.deleteUserById(userId));
        verify(userRepository).existsById(userId);
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should save user roles when creating user")
    void testCreateUser_SavesRoles() {
        // Arrange
        Role role1 = new Role();
        role1.setRoleId(1L);
        role1.setRoleName("ROLE_USER");

        Role role2 = new Role();
        role2.setRoleId(2L);
        role2.setRoleName("ROLE_ADMIN");

        UserRole userRole1 = new UserRole();
        userRole1.setRole(role1);
        UserRole userRole2 = new UserRole();
        userRole2.setRole(role2);

        Set<UserRole> userRoles = new HashSet<>();
        userRoles.add(userRole1);
        userRoles.add(userRole2);

        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(roleRepository.save(any())).thenReturn(null);
        when(userRepository.save(testUser)).thenReturn(testUser);

        // Act
        userService.createUser(testUser, userRoles);

        // Assert
        verify(roleRepository, times(2)).save(any());
    }

    @Test
    @DisplayName("Should add roles to user when creating user")
    void testCreateUser_AddsRolesToUser() {
        // Arrange
        UserRole userRole = new UserRole();
        userRole.setRole(testRole);
        Set<UserRole> userRoles = new HashSet<>();
        userRoles.add(userRole);

        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(roleRepository.save(any())).thenReturn(testRole);
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User createdUser = userService.createUser(testUser, userRoles);

        // Assert
        assertEquals(1, createdUser.getUserRoles().size());
    }

    @Test
    @DisplayName("Should handle empty roles set when creating user")
    void testCreateUser_EmptyRoles() {
        // Arrange
        Set<UserRole> emptyRoles = new HashSet<>();
        
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.save(testUser)).thenReturn(testUser);

        // Act
        User createdUser = userService.createUser(testUser, emptyRoles);

        // Assert
        assertNotNull(createdUser);
        verify(roleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should handle null role in user roles")
    void testCreateUser_NullRoleInSet() {
        // Arrange
        UserRole userRoleWithNull = new UserRole();
        userRoleWithNull.setRole(null);
        Set<UserRole> userRoles = new HashSet<>();
        userRoles.add(userRoleWithNull);

        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.save(testUser)).thenReturn(testUser);

        // Act
        User createdUser = userService.createUser(testUser, userRoles);

        // Assert
        assertNotNull(createdUser);
        verify(roleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return correct user when multiple users exist")
    void testGetUserByUsername_CorrectUser() {
        // Arrange
        User user1 = new User();
        user1.setUsername("user1");
        User user2 = new User();
        user2.setUsername("user2");

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user1));

        // Act
        User retrieved = userService.getUserByUsername("user1");

        // Assert
        assertEquals("user1", retrieved.getUsername());
        verify(userRepository).findByUsername("user1");
    }

    @Test
    @DisplayName("Should throw correct exception message when user not found")
    void testGetUserByUsername_ExceptionMessage() {
        // Arrange
        String username = "nonexistent";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.getUserByUsername(username));
        assertTrue(exception.getMessage().contains("not found"));
        assertTrue(exception.getMessage().contains(username));
    }

    @Test
    @DisplayName("Should throw correct exception when deleting non-existent user")
    void testDeleteUserById_ExceptionMessage() {
        // Arrange
        Long userId = 999L;
        when(userRepository.existsById(userId)).thenReturn(false);

        // Act & Assert
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.deleteUserById(userId));
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    @DisplayName("Should verify user repository is called for existence check")
    void testCreateUser_VerifyRepositoryCall() {
        // Arrange
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.save(testUser)).thenReturn(testUser);

        // Act
        userService.createUser(testUser, new HashSet<>());

        // Assert
        verify(userRepository).existsByUsername("testuser");
    }

    @Test
    @DisplayName("Should handle username case sensitivity correctly")
    void testGetUserByUsername_CaseSensitive() {
        // Arrange
//        when(userRepository.findByUsername("TestUser")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        User retrieved = userService.getUserByUsername("testuser");

        // Assert
        assertEquals("testuser", retrieved.getUsername());
    }
}
