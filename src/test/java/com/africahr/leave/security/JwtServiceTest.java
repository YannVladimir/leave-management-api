package com.africahr.leave.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        
        userDetails = User.builder()
                .username("test@example.com")
                .password("password")
                .authorities("ROLE_USER")
                .build();
    }

    @Test
    void generateToken_WithUserDetails_ReturnsValidToken() {
        String token = jwtService.generateToken(userDetails);
        
        assertNotNull(token);
        assertTrue(token.length() > 0);
        assertEquals(userDetails.getUsername(), jwtService.extractUsername(token));
    }

    @Test
    void generateToken_WithExtraClaims_ReturnsValidToken() {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", "ADMIN");
        
        String token = jwtService.generateToken(extraClaims, userDetails);
        
        assertNotNull(token);
        assertTrue(token.length() > 0);
        assertEquals(userDetails.getUsername(), jwtService.extractUsername(token));
    }

    @Test
    void isTokenValid_WithValidToken_ReturnsTrue() {
        String token = jwtService.generateToken(userDetails);
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void isTokenValid_WithInvalidUsername_ReturnsFalse() {
        String token = jwtService.generateToken(userDetails);
        
        UserDetails differentUser = User.builder()
                .username("different@example.com")
                .password("password")
                .authorities("ROLE_USER")
                .build();
        
        assertFalse(jwtService.isTokenValid(token, differentUser));
    }

    @Test
    void extractUsername_WithValidToken_ReturnsUsername() {
        String token = jwtService.generateToken(userDetails);
        String username = jwtService.extractUsername(token);
        
        assertEquals(userDetails.getUsername(), username);
    }
} 