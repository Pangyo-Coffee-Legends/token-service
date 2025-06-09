package com.nhnacademy.tokenservice.controller;

import com.nhnacademy.tokenservice.dto.JwtResponse;
import com.nhnacademy.tokenservice.dto.TokenRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import com.nhnacademy.tokenservice.dto.JwtIssueRequest;
import com.nhnacademy.tokenservice.service.impl.JwtTokenServiceImpl;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * JWT 토큰 관련 요청을 처리하는 REST 컨트롤러입니다.
 * - Access Token 및 Refresh Token 발급
 * - Refresh Token을 사용한 새로운 토큰 재발급
 * - 로그아웃 시 Access Token을 블랙리스트에 등록
 */
@RestController
@RequestMapping("/api/v1/token")
@RequiredArgsConstructor
public class JwtAuthController {

    private final JwtTokenServiceImpl jwtTokenService;

    /**
     * 사용자 이메일을 기반으로 Access Token과 Refresh Token을 발급합니다.
     *
     * @param request 발급 요청을 포함한 {@link JwtIssueRequest} 객체
     * @return 발급된 Access Token과 Refresh Token을 포함한 {@link JwtResponse}
     */
    @PostMapping
    public ResponseEntity<JwtResponse> issueToken(@RequestBody JwtIssueRequest request) {
        JwtResponse jwtResponse = jwtTokenService.issueToken(request);
        return ResponseEntity.ok(jwtResponse);
    }

    /**
     * 제공된 Refresh Token을 기반으로 새로운 Access Token과 Refresh Token을 재발급합니다.
     *
     * @param request 요청 본문에 포함된 {@link TokenRequest} 객체 (refreshToken)
     * @return 새로 발급된 Access Token과 Refresh Token을 포함한 {@link JwtResponse}
     */
    @PostMapping("/reissue")
    public ResponseEntity<JwtResponse> reissueToken(@Validated @RequestBody TokenRequest request){
        JwtResponse jwtResponse = jwtTokenService.reissueToken(request.getToken());
        return ResponseEntity.ok(jwtResponse);
    }

    /**
     * 사용자가 로그아웃을 할 때 Access Token을 블랙리스트에 등록하여 더 이상 사용하지 않도록 합니다.
     *
     * @param accessToken 요청 요청헤더에 포함된 객체 (authorization)
     * @return HTTP 200 OK 응답을 반환합니다.
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("authorization") String accessToken) {
        if(accessToken.startsWith("Bearer ")){
            accessToken = accessToken.substring(7);
        }
        jwtTokenService.blackListToken(accessToken);
        return ResponseEntity.ok().build();
    }
}
