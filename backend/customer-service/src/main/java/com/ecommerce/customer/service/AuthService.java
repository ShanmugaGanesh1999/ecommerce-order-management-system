package com.ecommerce.customer.service;

import com.ecommerce.customer.dto.*;
import com.ecommerce.customer.entity.Customer;
import com.ecommerce.customer.exception.AuthenticationException;
import com.ecommerce.customer.exception.DuplicateResourceException;
import com.ecommerce.customer.repository.CustomerRepository;
import com.ecommerce.customer.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for authentication operations.
 * Handles user registration, login, and token refresh.
 *
 * @author E-Commerce Team
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * Register a new customer.
     *
     * @param request registration request
     * @return authentication response with tokens
     * @throws DuplicateResourceException if email already exists
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new customer with email: {}", request.getEmail());

        // Check if email already exists
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered: " + request.getEmail());
        }

        // Create new customer
        Customer customer = Customer.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(Customer.Role.CUSTOMER)
                .build();

        customer = customerRepository.save(customer);
        log.info("Customer registered successfully with ID: {}", customer.getId());

        // Generate tokens
        String accessToken = jwtUtil.generateToken(customer);
        String refreshToken = jwtUtil.generateRefreshToken(customer);

        return buildAuthResponse(customer, accessToken, refreshToken);
    }

    /**
     * Authenticate a customer and generate tokens.
     *
     * @param request login request
     * @return authentication response with tokens
     * @throws AuthenticationException if credentials are invalid
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());

        // Find customer by email
        Customer customer = customerRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthenticationException("Invalid email or password"));

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), customer.getPassword())) {
            throw new AuthenticationException("Invalid email or password");
        }

        log.info("Customer logged in successfully with ID: {}", customer.getId());

        // Generate tokens
        String accessToken = jwtUtil.generateToken(customer);
        String refreshToken = jwtUtil.generateRefreshToken(customer);

        return buildAuthResponse(customer, accessToken, refreshToken);
    }

    /**
     * Refresh access token using refresh token.
     *
     * @param request refresh token request
     * @return authentication response with new tokens
     * @throws AuthenticationException if refresh token is invalid
     */
    @Transactional(readOnly = true)
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        try {
            String refreshToken = request.getRefreshToken();

            // Validate refresh token
            if (!jwtUtil.isRefreshToken(refreshToken)) {
                throw new AuthenticationException("Invalid refresh token");
            }

            String email = jwtUtil.extractUsername(refreshToken);

            // Find customer
            Customer customer = customerRepository.findByEmail(email)
                    .orElseThrow(() -> new AuthenticationException("Customer not found"));

            // Validate token
            if (!jwtUtil.validateToken(refreshToken, email)) {
                throw new AuthenticationException("Refresh token expired or invalid");
            }

            log.info("Token refreshed for customer ID: {}", customer.getId());

            // Generate new tokens
            String newAccessToken = jwtUtil.generateToken(customer);
            String newRefreshToken = jwtUtil.generateRefreshToken(customer);

            return buildAuthResponse(customer, newAccessToken, newRefreshToken);
        } catch (Exception e) {
            throw new AuthenticationException("Invalid refresh token", e);
        }
    }

    /**
     * Build authentication response with customer info and tokens.
     *
     * @param customer the customer
     * @param accessToken the access token
     * @param refreshToken the refresh token
     * @return authentication response
     */
    private AuthResponse buildAuthResponse(Customer customer, String accessToken, String refreshToken) {
        AuthResponse.CustomerDto customerDto = AuthResponse.CustomerDto.builder()
                .id(customer.getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .role(customer.getRole())
                .build();

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getExpiration())
                .customer(customerDto)
                .build();
    }
}
