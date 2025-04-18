package com.nhnacademy.jwtGenerator.dto;

import lombok.*;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JwtIssueRequest {
    private String email;
    private String role;
}
