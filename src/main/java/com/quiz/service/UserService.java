package com.quiz.service;

import com.quiz.entities.User;
import com.quiz.entities.UserRole;

import java.util.Set;

public interface UserService {

    User createUser(User user, Set<UserRole> userRoles);

    User getUserByUsername(String username);

    void deleteUserById(Long userId);
}
