
package com.ecommerce.api.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        String testSecret = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=";

        jwtService = new JwtService(testSecret);
    }

    @Test
    void shouldGenerateToken() {
        String token = jwtService.generateToken("test@example.com");

        assertTrue(token != null && !token.isBlank());
    }

    @Test
    void shouldExtractEmailFromToken() {
        String token = jwtService.generateToken("test@example.com");

        String email = jwtService.extractEmail(token);

        assertEquals("test@example.com", email);
    }

    @Test
    void shouldValidateTokenForCorrectEmail() {
        String email = "test@example.com";
        String token = jwtService.generateToken(email);

        boolean valid = jwtService.isTokenValid(token, email);

        assertTrue(valid);
    }

    @Test
    void shouldRejectTokenForDifferentEmail() {
        String token = jwtService.generateToken("test@example.com");

        boolean valid = jwtService.isTokenValid(
                token,
                "other@example.com");

        assertTrue(!valid);
    }

}
