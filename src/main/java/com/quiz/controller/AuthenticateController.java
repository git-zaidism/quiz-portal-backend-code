package com.quiz.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.quiz.constants.ApiPathConstants;
import com.quiz.dto.auth.AuthTokenRequest;
import com.quiz.dto.auth.AuthTokenResponse;
import com.quiz.dto.user.UserResponse;
import com.quiz.entities.User;
import com.quiz.mapper.UserMapper;
import com.quiz.service.AuthService;
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
@CrossOrigin("*")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "APIs for user authentication and token generation")
public class AuthenticateController {

    private final AuthService authService;
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping(ApiPathConstants.GENERATE_TOKEN)
    @Operation(summary = "Generate User Token", description = "Generate JWT token for user login using username and password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token generated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthTokenResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid username or password"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    public ResponseEntity<AuthTokenResponse> generateToken(@Valid @RequestBody AuthTokenRequest request) {
        log.info("Token generation requested for username={}", request.username());
        return ResponseEntity.ok(this.authService.generateToken(request.username(), request.password()));
    }

    @PostMapping(ApiPathConstants.GENERATE_ADMIN_TOKEN)
    @Operation(summary = "Generate Admin Token", description = "Generate JWT token for admin login using username and password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Admin token generated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthTokenResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials or user is not admin"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    public ResponseEntity<AuthTokenResponse> generateAdminToken(@Valid @RequestBody AuthTokenRequest request) {
        log.info("Admin token generation requested for username={}", request.username());
        return ResponseEntity.ok(this.authService.generateAdminToken(request.username(), request.password()));
    }

    @GetMapping(ApiPathConstants.CURRENT_USER)
    @Operation(summary = "Get Current User", description = "Retrieve the currently authenticated user's information")
    @SecurityRequirement(name = "bearer-jwt")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Current user information retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Access denied")
    })
    public UserResponse getCurrentAuthenticatedUser(Principal principal) {
        log.debug("Fetching current authenticated user for principal={}", principal.getName());
        User currentUser = this.userService.getUserByUsername(principal.getName());
        return this.userMapper.toResponse(currentUser);
    }
}
