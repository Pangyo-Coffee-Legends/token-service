package com.nhnacademy.tokenservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@ToString
@Getter
@NoArgsConstructor
public class JwtIssueRequest {

    private String email;

    private String role;

    public JwtIssueRequest(
            @JsonProperty("email") String email,
            @JsonProperty("role") String role
    ) {
        this.email = email;
        this.role = role;
    }
}
