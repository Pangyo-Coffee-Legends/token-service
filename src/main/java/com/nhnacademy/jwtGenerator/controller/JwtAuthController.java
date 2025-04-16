package com.nhnacademy.jwtGenerator.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import com.nhnacademy.jwtGenerator.dto.JwtIssueRequest;
import com.nhnacademy.jwtGenerator.service.JwtTokenService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class JwtAuthController {

    private final JwtTokenService jwtTokenService;

    @PostMapping("")
    public ResponseEntity<?> issueToken(@RequestBody JwtIssueRequest request) {
        String accessToken = jwtTokenService.createAccessToken(request.getEmail(), request.getRole());
        String refreshToken = jwtTokenService.createRefreshToken(request.getEmail());

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("accessToken", accessToken);
        tokenMap.put("refreshToken", refreshToken);

        return ResponseEntity.ok(tokenMap);
    }
}

