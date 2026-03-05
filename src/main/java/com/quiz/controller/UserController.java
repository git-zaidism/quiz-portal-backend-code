package com.quiz.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.quiz.dto.user.UserCreateRequest;
import com.quiz.dto.user.UserResponse;
import com.quiz.entities.User;
import com.quiz.mapper.UserMapper;
import com.quiz.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/user")
@CrossOrigin("*")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management", description = "APIs for user registration, retrieval, and deletion")
public class UserController {

    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @PostMapping
    @Operation(summary = "Create New User", description = "Register a new user in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request or user already exists"),
            @ApiResponse(responseCode = "409", description = "User with this username already exists")
    })
    public UserResponse createUser(@Valid @RequestBody UserCreateRequest request) {
        log.info("User creation requested for username={}", request.username());
        User user = this.userMapper.toEntity(request, this.passwordEncoder.encode(request.password()));
        User createdUser = this.userService.createUser(user, this.userMapper.toDefaultQuizzerRoles(user));
        log.info("User created successfully with userId={} username={}", createdUser.getId(), createdUser.getUsername());
        return this.userMapper.toResponse(createdUser);
    }

    @GetMapping("/{username}")
    @Operation(summary = "Get User by Username", description = "Retrieve user information by username")
    @SecurityRequirement(name = "bearer-jwt")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found and retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public UserResponse getUserByUsername(@PathVariable("username") String username) {
        log.debug("Fetching user by username={}", username);
        return this.userMapper.toResponse(this.userService.getUserByUsername(username));
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete User", description = "Delete a user by user ID")
    @SecurityRequirement(name = "bearer-jwt")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public void deleteUserById(@PathVariable("userId") Long userId) {
        log.info("User deletion requested for userId={}", userId);
        this.userService.deleteUserById(userId);
    }
}
