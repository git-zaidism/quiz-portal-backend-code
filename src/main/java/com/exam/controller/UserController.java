package com.exam.controller;

import com.exam.dto.user.UserCreateRequest;
import com.exam.dto.user.UserResponse;
import com.exam.mapper.UserMapper;
import com.exam.entities.User;
import com.exam.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@CrossOrigin("*")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @PostMapping
    public UserResponse createUser(@Valid @RequestBody UserCreateRequest request) {
        User user = this.userMapper.toEntity(request, this.passwordEncoder.encode(request.password()));
        User createdUser = this.userService.createUser(user, this.userMapper.toDefaultNormalRoles(user));
        return this.userMapper.toResponse(createdUser);
    }

    @GetMapping("/{username}")
    public UserResponse getUserByUsername(@PathVariable("username") String username) {
        return this.userMapper.toResponse(this.userService.getUserByUsername(username));
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable("userId") Long userId) {
        this.userService.deleteUserById(userId);
    }
}
