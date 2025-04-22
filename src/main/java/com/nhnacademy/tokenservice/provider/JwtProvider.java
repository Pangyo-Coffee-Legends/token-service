package com.nhnacademy.tokenservice.provider;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtProvider {

    @Value("${jwt.access.expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh.expiration}")
    private long refreshTokenExpiration;

    private final Key key;

    public JwtProvider(@Value("${jwt.secret}") String secretKey) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(String email){
        return Jwts.builder()
                .issuer("판교스타벅스에서봅시다") // jwt 발급한 주체
                .subject("AccessToken")
                .claim("email", email)
//                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration)) // 1시간
                .signWith(key)
                .compact();
    }

    public String createRefreshToken(String email){
        return Jwts.builder()
                .issuer("판교스타벅스에서봅시다")
                .subject("RefreshToken")
                .claim("email", email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpiration)) // 7일
                .signWith(key)
                .compact();
    }

    public Claims extractClaims(String token){
        return Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public long getExpiration(String token) {
        long exp = Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration()
                .getTime();

        long now = System.currentTimeMillis();

        return exp - now;
    }

}
