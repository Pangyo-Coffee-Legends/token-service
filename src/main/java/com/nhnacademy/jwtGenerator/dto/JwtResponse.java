package com.nhnacademy.jwtGenerator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class JwtResponse {
    @JsonProperty("accessToken")
    String accessToken;

    @JsonProperty("refreshToken")
    String refreshToken;
}
