package com.nhnacademy.tokenservice.provider;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
public class JwtProvider {

    @Value("${jwt.access.expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh.expiration}")
    private long refreshTokenExpiration;

    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public JwtProvider(@Value("${jwt.private.secret}") String privateKeyPem,
                       @Value("${jwt.public.secret}") String publicKeyPem) throws Exception {
        this.privateKey = loadPrivateKey(privateKeyPem);
        this.publicKey = loadPublicKey(publicKeyPem);
    }

    // RS256 쓴 '이유', 특징, 안전하게 처리하는지.. -> RSA방식 쓰고 있음
    // 회원 번호로 하는 것도 생각...
    public String createAccessToken(String email, String role) {
        return Jwts.builder()
                .issuer("판교스타벅스에서봅시다")
                .subject("AccessToken")
                .claim("email", email)
                .claim("roles", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    public String createRefreshToken(String email) {
        return Jwts.builder()
                .issuer("판교스타벅스에서봅시다")
                .subject("RefreshToken")
                .claim("email", email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public long getExpiration(String token) {
        Date expiration = extractClaims(token).getExpiration();
        return expiration.getTime() - System.currentTimeMillis();
    }

    private PrivateKey loadPrivateKey(String key) throws Exception {
        String privateKey = key.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] encoded = Base64.getDecoder().decode(privateKey);
        return KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(encoded));
    }

    private PublicKey loadPublicKey(String key) throws Exception {
        String publicKey = key.replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] encoded = Base64.getDecoder().decode(publicKey);
        return KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(encoded));
    }
}
