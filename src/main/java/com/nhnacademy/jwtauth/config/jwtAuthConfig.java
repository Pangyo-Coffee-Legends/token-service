package com.nhnacademy.jwtauth.config;

import com.nhnacademy.jwtauth.exceptionhandling.CustomAccessDeniedHandler;
import com.nhnacademy.jwtauth.filter.JWTTokenGeneratorFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class jwtAuthConfig {
    @Bean
    SecurityFilterChain jwtFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrfConfig -> csrfConfig.disable())
                .sessionManagement(sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(corsConfig -> corsConfig.configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration config = new CorsConfiguration();
                        // setAllowedOrigins() 에는 허용할 출처만 적음. / 출처 모두 허용으로 설정.
                        config.setAllowedOrigins(Collections.singletonList("*"));
                        // setAllowedMethods는 브라우저가 특정 http 메서드에 대해서만 허용하는 메서드 / 모두 허용하고 있다.
                        config.setAllowedMethods(Collections.singletonList("*"));
                        // 브라우저가 백엔드 api에 요청을 보낼 때 자격 증명이나 적용 가능한 쿠키를 전송할 수 있도록 설정
                        config.setAllowCredentials(true);
                        // UI 애플리케이션이나 다른 출처에서 백엔드가 수락할 수 있는 헤더 목록을 정의
                        config.setAllowedHeaders(Collections.singletonList("*"));
                        config.setExposedHeaders(Arrays.asList("Authorization"));
                        // 브라우저에게 이 모든 설정을 1시간 동안 기억하라고 지시
                        config.setMaxAge(3600L);
                        return config;
                    }
                }))
                .addFilterAfter(new JWTTokenGeneratorFilter(), BasicAuthenticationFilter.class)
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/myAccount", "/api/auth/member").authenticated()
                        .requestMatchers("/","/error","/api/auth/register").permitAll());
        http.httpBasic(withDefaults());

        http.exceptionHandling(ehc -> ehc.accessDeniedHandler(new CustomAccessDeniedHandler()));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

//    @Bean
//    public CompromisedPasswordChecker compromisedPasswordChecker() {
//        return new HaveIBeenPwnedRestApiPasswordChecker();
//    }
}

