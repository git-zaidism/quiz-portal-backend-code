package com.quiz.controller;

import com.quiz.constants.ApiPathConstants;
import com.quiz.dto.auth.AuthTokenRequest;
import com.quiz.dto.auth.AuthTokenResponse;
import com.quiz.dto.user.UserResponse;
import com.quiz.entities.User;
import com.quiz.mapper.UserMapper;
import com.quiz.service.AuthService;
import com.quiz.service.UserService;
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

    @PostMapping(ApiPathConstants.GENERATE_TOKEN)
    public ResponseEntity<AuthTokenResponse> generateToken(@Valid @RequestBody AuthTokenRequest request) {
        return ResponseEntity.ok(this.authService.generateToken(request.username(), request.password()));
    }

    @PostMapping(ApiPathConstants.GENERATE_ADMIN_TOKEN)
    public ResponseEntity<AuthTokenResponse> generateAdminToken(@Valid @RequestBody AuthTokenRequest request) {
        return ResponseEntity.ok(this.authService.generateAdminToken(request.username(), request.password()));
    }

    @GetMapping(ApiPathConstants.CURRENT_USER)
    public UserResponse getCurrentAuthenticatedUser(Principal principal) {
        User currentUser = this.userService.getUserByUsername(principal.getName());
        return this.userMapper.toResponse(currentUser);
    }
}
