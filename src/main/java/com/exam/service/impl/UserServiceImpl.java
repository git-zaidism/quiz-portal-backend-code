package com.exam.service.impl;

import com.exam.exception.UserFoundException;
import com.exam.exception.UserNotFoundException;
import com.exam.entities.User;
import com.exam.entities.UserRole;
import com.exam.repo.RoleRepository;
import com.exam.repo.UserRepository;
import com.exam.service.UserService;
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
            throw new UserFoundException();
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
