package com.exam.mapper;

import com.exam.dto.user.UserCreateRequest;
import com.exam.dto.user.UserResponse;
import com.exam.entities.Role;
import com.exam.entities.User;
import com.exam.entities.UserRole;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    private static final long NORMAL_ROLE_ID = 45L;
    private static final String NORMAL_ROLE_NAME = "NORMAL";

    public User toEntity(UserCreateRequest request, String encodedPassword) {
        User user = new User();
        user.setUsername(request.username());
        user.setPassword(encodedPassword);
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        user.setProfile("default.png");
        return user;
    }

    public Set<UserRole> toDefaultNormalRoles(User user) {
        Set<UserRole> userRoles = new HashSet<>();

        Role role = new Role();
        role.setRoleId(NORMAL_ROLE_ID);
        role.setRoleName(NORMAL_ROLE_NAME);

        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);
        userRoles.add(userRole);

        return userRoles;
    }

    public UserResponse toResponse(User user) {
        Set<String> roles = user.getUserRoles().stream()
                .map(UserRole::getRole)
                .map(Role::getRoleName)
                .collect(Collectors.toSet());

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                user.isEnabled(),
                user.getProfile(),
                roles
        );
    }
}
