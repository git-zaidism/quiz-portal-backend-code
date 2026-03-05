package com.quiz.service.impl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.quiz.entities.User;
import com.quiz.repositoy.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserDetailsServiceImpl Tests")
class UserDetailsServiceImplTest {

    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        userDetailsService = new UserDetailsServiceImpl(userRepository);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("encoded_password");
        testUser.setEmail("test@example.com");
        testUser.setEnabled(true);
    }

    @Test
    @DisplayName("Should load user details by username successfully")
    void testLoadUserByUsername_Success() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Assert
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user not found")
    void testLoadUserByUsername_NotFound() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, 
                () -> userDetailsService.loadUserByUsername("nonexistent"));
    }

    @Test
    @DisplayName("Should throw exception with correct message when user not found")
    void testLoadUserByUsername_CorrectExceptionMessage() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, 
                () -> userDetailsService.loadUserByUsername("nonexistent"));
        assertTrue(exception.getMessage().contains("User not found"));
    }

    @Test
    @DisplayName("Should call repository with correct username")
    void testLoadUserByUsername_CallsRepository() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        userDetailsService.loadUserByUsername("testuser");

        // Assert
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    @DisplayName("Should load user with correct username for login")
    void testLoadUserByUsername_CorrectUsername() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Assert
        assertEquals("testuser", userDetails.getUsername());
    }

    @Test
    @DisplayName("Should load enabled user")
    void testLoadUserByUsername_EnabledUser() {
        // Arrange
        testUser.setEnabled(true);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Assert
        assertTrue(userDetails.isEnabled());
    }

    @Test
    @DisplayName("Should handle username with special characters")
    void testLoadUserByUsername_SpecialCharacters() {
        // Arrange
        String specialUsername = "test.user@domain";
        User specialUser = new User();
        specialUser.setUsername(specialUsername);
        when(userRepository.findByUsername(specialUsername)).thenReturn(Optional.of(specialUser));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername(specialUsername);

        // Assert
        assertEquals(specialUsername, userDetails.getUsername());
        verify(userRepository).findByUsername(specialUsername);
    }

    @Test
    @DisplayName("Should handle case sensitive username lookup")
    void testLoadUserByUsername_CaseSensitive() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.findByUsername("TESTUSER")).thenReturn(Optional.empty());

        // Act & Assert
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");
        assertNotNull(userDetails);

        assertThrows(UsernameNotFoundException.class, 
                () -> userDetailsService.loadUserByUsername("TESTUSER"));
    }

    @Test
    @DisplayName("Should return user with correct password")
    void testLoadUserByUsername_CorrectPassword() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Assert
        assertEquals("encoded_password", userDetails.getPassword());
    }

    @Test
    @DisplayName("Should throw exception for null username")
    void testLoadUserByUsername_NullUsername() {
        // Act & Assert
        assertThrows(Exception.class, () -> userDetailsService.loadUserByUsername(null));
    }

    @Test
    @DisplayName("Should throw exception for empty username")
    void testLoadUserByUsername_EmptyUsername() {
        // Arrange
        when(userRepository.findByUsername("")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, 
                () -> userDetailsService.loadUserByUsername(""));
    }

    @Test
    @DisplayName("Should load user multiple times independently")
    void testLoadUserByUsername_MultipleTimes() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        UserDetails first = userDetailsService.loadUserByUsername("testuser");
        UserDetails second = userDetailsService.loadUserByUsername("testuser");

        // Assert
        assertEquals(first.getUsername(), second.getUsername());
    }

    @Test
    @DisplayName("Should implement UserDetailsService interface")
    void testImplementsUserDetailsService() {
        // Assert
        assertTrue(userDetailsService instanceof org.springframework.security.core.userdetails.UserDetailsService);
    }
}
