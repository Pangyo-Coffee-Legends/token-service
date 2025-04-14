package com.nhnacademy.jwtauth.config;

import com.nhnacademy.jwtauth.model.domain.Member;
import com.nhnacademy.jwtauth.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomMemberDetailsService implements UserDetailsService {
    private final MemberRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String mbEmail) throws UsernameNotFoundException {
        Member member = customerRepository.findBymbEmail(mbEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Member not found."));
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(member.getRole().getRoleName()));

        return new User(member.getMbEmail(), member.getMbPassword(), authorities);
    }
}