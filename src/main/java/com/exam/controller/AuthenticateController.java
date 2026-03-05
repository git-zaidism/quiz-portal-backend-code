package com.exam.controller;

import com.exam.dto.auth.AuthTokenRequest;
import com.exam.dto.auth.AuthTokenResponse;
import com.exam.dto.user.UserResponse;
import com.exam.entities.User;
import com.exam.mapper.UserMapper;
import com.exam.service.AuthService;
import com.exam.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
public class AuthenticateController {

    private final AuthService authService;
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/generate-token")
    public ResponseEntity<AuthTokenResponse> generateToken(@Valid @RequestBody AuthTokenRequest request) {
        return ResponseEntity.ok(this.authService.generateToken(request.username(), request.password()));
    }

    @PostMapping("/generate-admin-token")
    public ResponseEntity<AuthTokenResponse> generateAdminToken(@Valid @RequestBody AuthTokenRequest request) {
        return ResponseEntity.ok(this.authService.generateAdminToken(request.username(), request.password()));
    }

    @GetMapping("/current-user")
    public UserResponse getCurrentAuthenticatedUser(Principal principal) {
        User currentUser = this.userService.getUserByUsername(principal.getName());
        return this.userMapper.toResponse(currentUser);
    }
}
