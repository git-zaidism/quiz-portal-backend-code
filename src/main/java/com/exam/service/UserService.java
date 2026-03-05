package com.exam.service;

import com.exam.entities.User;
import com.exam.entities.UserRole;

import java.util.Set;

public interface UserService {

    User createUser(User user, Set<UserRole> userRoles);

    User getUserByUsername(String username);

    void deleteUserById(Long userId);
}
