package com.nhnacademy.jwtauth.repository;

import com.nhnacademy.jwtauth.model.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findBymbEmail(String userEmail);
}
