package com.quiz.service.impl;

import com.quiz.exception.UserAlreadyExistsException;
import com.quiz.exception.UserNotFoundException;
import com.quiz.entities.User;
import com.quiz.entities.UserRole;
import com.quiz.repositoy.RoleRepository;
import com.quiz.repositoy.UserRepository;
import com.quiz.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public User createUser(User user, Set<UserRole> userRoles) {
        log.info("Creating user with username={}", user.getUsername());
        if (this.userRepository.existsByUsername(user.getUsername())) {
            log.warn("User creation failed, username already exists: {}", user.getUsername());
            throw new UserAlreadyExistsException();
        }

        userRoles.stream()
                .map(UserRole::getRole)
                .filter(Objects::nonNull)
                .forEach(this.roleRepository::save);
        log.debug("Persisted {} roles for username={}", userRoles.size(), user.getUsername());

        user.getUserRoles().addAll(userRoles);
        User createdUser = this.userRepository.save(user);
        log.info("User created successfully with userId={} username={}", createdUser.getId(), createdUser.getUsername());
        return createdUser;
    }

    @Override
    public User getUserByUsername(String username) {
        log.debug("Loading user by username={}", username);
        return this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
    }

    @Override
    public void deleteUserById(Long userId) {
        log.info("Deleting user by userId={}", userId);
        if (!this.userRepository.existsById(userId)) {
            log.warn("User deletion failed, userId not found: {}", userId);
            throw new UserNotFoundException("User not found with id: " + userId);
        }
        this.userRepository.deleteById(userId);
        log.info("Deleted user successfully userId={}", userId);
    }
}
