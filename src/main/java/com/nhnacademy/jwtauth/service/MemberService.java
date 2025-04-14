package com.nhnacademy.jwtauth.service;

import com.nhnacademy.jwtauth.model.domain.Member;
import com.nhnacademy.jwtauth.model.domain.Role;
import com.nhnacademy.jwtauth.model.dto.RegisterRequestDto;
import com.nhnacademy.jwtauth.repository.RoleRepository;
import com.nhnacademy.jwtauth.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public void registermember(RegisterRequestDto request) {
        // 역할 조회
        Role role = roleRepository.findByRoleName(request.getRole())
                .orElseThrow(() -> new IllegalArgumentException("Invalid role: " + request.getRole()));

        // 사용자 생성 및 저장
        Member member = Member.ofNewMember(
                request.getMbName(),
                request.getMbEmail(),
                passwordEncoder.encode(request.getMbPassword()), // 비밀번호 암호화
                request.getPhoneNumber()
        );

        member.assignRole(role);

        memberRepository.save(member);
    }

    public Member findByUserEmail(String mbEmail) {
        return memberRepository.findBymbEmail(mbEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + mbEmail));
    }
}
