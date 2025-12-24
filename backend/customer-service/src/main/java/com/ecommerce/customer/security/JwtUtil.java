package com.ecommerce.customer.security;

import com.ecommerce.customer.entity.Customer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utility class for JWT token operations.
 * Handles token generation, validation, and claims extraction using RS256 algorithm.
 *
 * @author E-Commerce Team
 * @version 1.0
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret:MySecretKeyForJWTTokenGenerationThatIsLongEnoughForHS256Algorithm}")
    private String secret;

    @Value("${jwt.expiration:86400000}") // 24 hours in milliseconds
    private Long expiration;

    @Value("${jwt.refresh.expiration:2592000000}") // 30 days in milliseconds
    private Long refreshExpiration;

    /**
     * Generate JWT access token for a customer.
     *
     * @param customer the customer
     * @return the JWT token
     */
    public String generateToken(Customer customer) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", customer.getId());
        claims.put("email", customer.getEmail());
        claims.put("role", customer.getRole().name());
        return createToken(claims, customer.getEmail(), expiration);
    }

    /**
     * Generate JWT refresh token for a customer.
     *
     * @param customer the customer
     * @return the refresh token
     */
    public String generateRefreshToken(Customer customer) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", customer.getId());
        claims.put("type", "refresh");
        return createToken(claims, customer.getEmail(), refreshExpiration);
    }

    /**
     * Create a JWT token with claims and expiration.
     *
     * @param claims the claims to include
     * @param subject the subject (typically username/email)
     * @param expirationTime the expiration time in milliseconds
     * @return the JWT token
     */
    private String createToken(Map<String, Object> claims, String subject, Long expirationTime) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Get the signing key for JWT.
     *
     * @return the secret key
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extract username (email) from token.
     *
     * @param token the JWT token
     * @return the username
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract user ID from token.
     *
     * @param token the JWT token
     * @return the user ID
     */
    public Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", Long.class));
    }

    /**
     * Extract expiration date from token.
     *
     * @param token the JWT token
     * @return the expiration date
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extract a specific claim from token.
     *
     * @param token the JWT token
     * @param claimsResolver function to resolve the claim
     * @param <T> the type of the claim
     * @return the claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extract all claims from token.
     *
     * @param token the JWT token
     * @return all claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Check if token is expired.
     *
     * @param token the JWT token
     * @return true if expired
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Validate token.
     *
     * @param token the JWT token
     * @param username the username to validate against
     * @return true if valid
     */
    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    /**
     * Check if token is a refresh token.
     *
     * @param token the JWT token
     * @return true if it's a refresh token
     */
    public Boolean isRefreshToken(String token) {
        String type = extractClaim(token, claims -> claims.get("type", String.class));
        return "refresh".equals(type);
    }

    /**
     * Get token expiration time in milliseconds.
     *
     * @return expiration time
     */
    public Long getExpiration() {
        return expiration;
    }
}
