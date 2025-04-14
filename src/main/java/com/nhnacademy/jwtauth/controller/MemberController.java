package com.nhnacademy.jwtauth.controller;

import com.nhnacademy.jwtauth.model.domain.Member;
import com.nhnacademy.jwtauth.model.dto.RegisterRequestDto;
import com.nhnacademy.jwtauth.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/register")
    public ResponseEntity<String> registerMember(@RequestBody RegisterRequestDto request) {
        memberService.registermember(request);
        return ResponseEntity.ok("회원가입 성공!");
    }

    @GetMapping("/member")
    public ResponseEntity<?> getMemberInfo(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        String email = authentication.getName();
        Member member = memberService.findByUserEmail(email);
        if (member == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("userEmail", member.getMbEmail());
        response.put("userName", member.getMbName());
        response.put("phoneNumber", member.getPhoneNumber());
        response.put("role", member.getRole().getRoleName());

        return ResponseEntity.ok(response);
    }

}
