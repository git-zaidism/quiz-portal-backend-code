package com.quiz.controller;

import com.quiz.dto.user.UserCreateRequest;
import com.quiz.dto.user.UserResponse;
import com.quiz.mapper.UserMapper;
import com.quiz.entities.User;
import com.quiz.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@CrossOrigin("*")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @PostMapping
    public UserResponse createUser(@Valid @RequestBody UserCreateRequest request) {
        log.info("User creation requested for username={}", request.username());
        User user = this.userMapper.toEntity(request, this.passwordEncoder.encode(request.password()));
        User createdUser = this.userService.createUser(user, this.userMapper.toDefaultQuizzerRoles(user));
        log.info("User created successfully with userId={} username={}", createdUser.getId(), createdUser.getUsername());
        return this.userMapper.toResponse(createdUser);
    }

    @GetMapping("/{username}")
    public UserResponse getUserByUsername(@PathVariable("username") String username) {
        log.debug("Fetching user by username={}", username);
        return this.userMapper.toResponse(this.userService.getUserByUsername(username));
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable("userId") Long userId) {
        log.info("User deletion requested for userId={}", userId);
        this.userService.deleteUserById(userId);
    }
}
