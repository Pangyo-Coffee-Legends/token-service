package com.nhnacademy.tokenservice.provider;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
class JwtProviderTest {

    @Autowired
    private JwtProvider provider;

    @Test
    @DisplayName("access token 발급")
    void createAccessToken() {
        String token = provider.createAccessToken("test@test.com", "ROLE_USER");

        assertNotNull(token);
    }

    @Test
    @DisplayName("refresh token 발급")
    void createRefreshToken() {
        String token = provider.createRefreshToken("test@test.com");

        assertNotNull(token);
    }

    @Test
    @DisplayName("claim 추출")
    void extractClaims() {
        String token = provider.createAccessToken("test@test.com", "ROLE_USER");

        String email = (String) provider.extractClaims(token).get("email");

        assertNotNull(email);
        assertEquals("test@test.com", email);
    }

    @Test
    @DisplayName("유효시간 추출")
    void getExpiration() {
        String token = provider.createAccessToken("test@test.com", "ROLE_USER");
        long exp = provider.getExpiration(token);

        assertNotNull(exp);
    }
}