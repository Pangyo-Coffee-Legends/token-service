package com.nhnacademy.jwtauth.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RegisterRequestDto {
    private String mbName;       // 사용자 이름
    private String mbEmail;      // 사용자 이메일
    private String mbPassword;   // 사용자 비밀번호
    private String phoneNumber;    // 사용자 전화번호
    private String role;       // 역할 이름 (예: "USER")
}
