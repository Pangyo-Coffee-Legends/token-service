package com.nhnacademy.jwtGenerator.service;

import com.nhnacademy.jwtGenerator.constants.ApplicationConstants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtTokenService {

    private String secretKey = ApplicationConstants.JWT_SECRET_DEFAULT_VALUE;

    public String createAccessToken(String email, String role) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .issuer("판교커피레전드")
                .subject("AccessToken")
                .claim("email", email)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000L * 60 * 30)) // 30분
                .signWith(key)
                .compact();
    }

    public String createRefreshToken(String email) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .issuer("판교커피레전드")
                .subject("RefreshToken")
                .claim("email", email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7)) // 7일
                .signWith(key)
                .compact();
    }
}
