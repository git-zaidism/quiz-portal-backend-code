package com.quiz.service.impl;

import com.quiz.exception.UserAlreadyExistsException;
import com.quiz.exception.UserNotFoundException;
import com.quiz.entities.User;
import com.quiz.entities.UserRole;
import com.quiz.repositoy.RoleRepository;
import com.quiz.repositoy.UserRepository;
import com.quiz.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public User createUser(User user, Set<UserRole> userRoles) {
        if (this.userRepository.existsByUsername(user.getUsername())) {
            throw new UserAlreadyExistsException();
        }

        userRoles.stream()
                .map(UserRole::getRole)
                .filter(Objects::nonNull)
                .forEach(this.roleRepository::save);

        user.getUserRoles().addAll(userRoles);
        return this.userRepository.save(user);
    }

    @Override
    public User getUserByUsername(String username) {
        return this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
    }

    @Override
    public void deleteUserById(Long userId) {
        if (!this.userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }
        this.userRepository.deleteById(userId);
    }
}
