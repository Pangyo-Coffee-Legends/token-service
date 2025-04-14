package com.nhnacademy.jwtauth.filter;

import com.nhnacademy.jwtauth.constants.ApplicationConstants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

public class JWTTokenGeneratorFilter extends OncePerRequestFilter {
    // 이 섹션에서는 OncePerRequestFilter 접근 방식을 사용한다.
    // 요청의 일부로 필터가 한 번만 실행되어야 하는 타당한 비즈니석 이유가 있기 때문이다.
    // 요청의 일부로 필터가 여러 번 호출되는 경우 여러번 JWT 토큰을 생성하고 싶지 않기 때문에 OncePerRequestFilter을 사용하고 있다.
    // 우리는 이 필터가 로그인 작업 중에만 실행되기를 원한다. 초기 로그인이 작업이 완료되면 JWT 토큰을 생성하고 싶다.
    // 이후의 모든 요청은 필터가 호출되지 않기를 원한다. 왜냐하면 이후 모든 요청은 토큰을 생성하는 것이 아니라
    // 토큰을 검증하기를 원하기 때문이다. 특정 시나리오에서 이 필터를 실행하지 않도록 Spring Boot 프레임워크에 어떻게 전달할까?

    /**
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(null != authentication) {
            Environment env = getEnvironment();
            if(env != null) {
                String secret = env.getProperty(ApplicationConstants.JWT_SECRET_KEY,
                        ApplicationConstants.JWT_SECRET_DEFAULT_VALUE);
                SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

                // JWT Token 생성
                String jwt = Jwts.builder().issuer("판교커피레전드").subject("JWT Token")
                        .claim("username", authentication.getName())
                        .claim("authorities", authentication.getAuthorities().stream().map(
                                GrantedAuthority::getAuthority).collect(Collectors.joining(",")))
                        .issuedAt(new Date())
                        .expiration(new Date((new Date()).getTime() + 30000000))
                        .signWith(secretKey).compact();

                // Refresh Token 생성
                String refreshToken = Jwts.builder().issuer("판교커피레전드").subject("Refresh Token")
                        .claim("userId", authentication.getName()) // userEmail 또는 ID (고유)
                        .issuedAt(new Date())
                        .expiration(new Date((new Date()).getTime() + 1000L * 60 * 60 * 24 * 7)) // 7일
                        .signWith(secretKey).compact();

                response.setHeader(ApplicationConstants.JWT_HEADER, jwt);
                response.addHeader("Access-Control-Expose-Headers", "Authorization");
                response.setHeader("Refresh-Token", jwt);


                Cookie cookie = new Cookie("Authorization", jwt);
                cookie.setHttpOnly(true); // 클라이언트 측 스크립트 접근 불가능 (보안 강화)
                cookie.setSecure(false);  // 로컬 개발 환경에서는 false, 운영 환경에서는 true
                cookie.setPath("/");      // 모든 경로에 대해 유효하도록 설정
                cookie.setMaxAge(60 * 60); // 1시간 동안 유효
                response.addCookie(cookie);
                System.out.println("dfdf" + cookie);

                // 쿠키에 저장
                Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
                refreshCookie.setHttpOnly(true);
                refreshCookie.setSecure(false); // 운영 시 true
                refreshCookie.setPath("/");
                refreshCookie.setMaxAge(60 * 60 * 24 * 7); // 7일
                response.addCookie(refreshCookie);
            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // shouldNotFilter가 false를 반환하면 doFilterInternal 해당 필터가 실행되고 -> /api/auth/user 일때 jwt 생성
        // true를 반환하면 doFilterInternal 해당 필터가 실행되지 않는다.
        // request.getServletPath() 이건 클라이언트 애플리케이션이 호출한 현재 경로를 알려준다.
        // 여기서 /api/auth/user는 로그인 api 경로임. -> 로그인 할때만 이 필터를 실행하고 나머지 시나리오에서는 이 필터가 실행이 되지 않게 설정.
        return !request.getServletPath().equals("/api/auth/member");
    }
}
