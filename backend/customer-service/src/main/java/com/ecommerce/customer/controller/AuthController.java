package com.ecommerce.customer.controller;

import com.ecommerce.customer.dto.AuthResponse;
import com.ecommerce.customer.dto.LoginRequest;
import com.ecommerce.customer.dto.RefreshTokenRequest;
import com.ecommerce.customer.dto.RegisterRequest;
import com.ecommerce.customer.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication endpoints.
 * Handles user registration, login, and token refresh.
 *
 * @author E-Commerce Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {

    private final AuthService authService;

    /**
     * Register a new customer.
     *
     * @param request registration request
     * @return authentication response with tokens
     */
    @PostMapping("/register")
    @Operation(summary = "Register a new customer", description = "Creates a new customer account and returns JWT tokens")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Login with email and password.
     *
     * @param request login request
     * @return authentication response with tokens
     */
    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate with email and password, returns JWT tokens")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Refresh access token using refresh token.
     *
     * @param request refresh token request
     * @return authentication response with new tokens
     */
    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Get new access and refresh tokens using a valid refresh token")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }
}
