package com.nhnacademy.tokenservice.service;

import com.nhnacademy.tokenservice.dto.JwtResponse;

/**
 * JWT 토큰 관련 핵심 기능을 정의한 서비스 인터페이스입니다.
 * - Access Token 및 Refresh Token 발급
 * - Refresh Token 기반 재발급
 * - Access Token 블랙리스트 처리 등
 */
public interface JwtTokenService {
    /**
     * 사용자의 이메일을 기반으로 Access Token 및 Refresh Token을 발급합니다.
     *
     * @param email 토큰을 발급할 사용자의 이메일
     * @return Access Token과 Refresh Token이 포함된 {@link JwtResponse}
     */
    JwtResponse issueToken(String email);

    /**
     * 전달받은 Refresh Token을 기반으로 새로운 Access Token과 Refresh Token을 재발급합니다.
     * 유효하지 않거나 만료된 Refresh Token일 경우 예외를 발생시킵니다.
     *
     * @param refreshToken 클라이언트로부터 전달받은 Refresh Token
     * @return 새로 발급된 Access Token과 Refresh Token이 포함된 {@link JwtResponse}
     */
    JwtResponse reissueToken(String refreshToken);

    /**
     * 주어진 Access Token을 블랙리스트에 등록하여 더 이상 사용하지 못하도록 처리합니다.
     * 로그아웃 처리 시 사용됩니다.
     *
     * @param accessToken 블랙리스트에 등록할 Access Token
     */
    void blackListToken(String accessToken);
}