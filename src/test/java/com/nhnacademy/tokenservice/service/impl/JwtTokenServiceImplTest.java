package com.nhnacademy.tokenservice.service.impl;

import com.nhnacademy.tokenservice.dao.RedisDao;
import com.nhnacademy.tokenservice.dto.JwtResponse;
import com.nhnacademy.tokenservice.provider.JwtProvider;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class JwtTokenServiceImplTest {

    @Mock
    private RedisDao redisDao;
    @Mock
    private JwtProvider provider;

    @InjectMocks
    private JwtTokenServiceImpl jwtTokenService;
    private static final String REDIS_KEY_TOKEN_PREFIX="refresh:user-email:";

    private String email = "test@example.com";
    private String accessToken = "access-token";
    private String refreshToken = "refresh-token";

    @Test
    @DisplayName("token 발급")
    void issueToken() {
        when(provider.createAccessToken(email)).thenReturn(accessToken);
        when(provider.createRefreshToken(email)).thenReturn(refreshToken);
        when(provider.getExpiration(refreshToken)).thenReturn(3600L);

        JwtResponse response = jwtTokenService.issueToken(email);

        verify(provider, Mockito.times(1)).createAccessToken(Mockito.anyString());
        verify(provider, Mockito.times(1)).createRefreshToken(Mockito.anyString());
        verify(redisDao, Mockito.times(1)).save(REDIS_KEY_TOKEN_PREFIX + email, refreshToken, 3600L);

        assertEquals(accessToken, response.getAccessToken());
        assertEquals(refreshToken, response.getRefreshToken());
    }

    @Test
    @DisplayName("token 재발급")
    void reissueToken() {
        String key = REDIS_KEY_TOKEN_PREFIX + email;
        String newAccessToken = "new-access-token";
        String newRefreshToken = "new-refresh-token";

        Claims claims = mock(Claims.class);
        when(claims.get("email")).thenReturn(email);
        when(provider.extractClaims(refreshToken)).thenReturn(claims);

        when(redisDao.has(key)).thenReturn(true);
        when(redisDao.get(key)).thenReturn(refreshToken);

        when(provider.createAccessToken(email)).thenReturn(newAccessToken);
        when(provider.createRefreshToken(email)).thenReturn(newRefreshToken);
        when(provider.getExpiration(newRefreshToken)).thenReturn(3600L);

        JwtResponse response = jwtTokenService.reissueToken(refreshToken);

        verify(redisDao, Mockito.times(1)).has(key);
        verify(redisDao, Mockito.times(1)).get(key);
        verify(provider, Mockito.times(1)).createAccessToken(email);
        verify(provider, Mockito.times(1)).createRefreshToken(email);
        verify(redisDao, Mockito.times(1)).save(key, newRefreshToken, 3600L);

        assertEquals(newAccessToken, response.getAccessToken());
        assertEquals(newRefreshToken, response.getRefreshToken());
    }

    @Test
    @DisplayName("블랙리스트")
    void blackListToken() {
        String key = "blacklist:" + accessToken;
        when(provider.getExpiration(accessToken)).thenReturn(3600L);

        Claims claims = mock(Claims.class);
        when(claims.get("email")).thenReturn(email);
        when(provider.extractClaims(accessToken)).thenReturn(claims);

        when(redisDao.has(REDIS_KEY_TOKEN_PREFIX + email)).thenReturn(true);

        jwtTokenService.blackListToken(accessToken);

        verify(redisDao, Mockito.times(1)).has(REDIS_KEY_TOKEN_PREFIX + email);
        verify(redisDao, Mockito.times(1)).delete(REDIS_KEY_TOKEN_PREFIX + email);
        verify(redisDao, Mockito.times(1)).save(key, "logout", 3600L);
    }
}