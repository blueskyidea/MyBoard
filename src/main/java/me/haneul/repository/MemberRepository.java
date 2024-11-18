package me.haneul.repository;

import me.haneul.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {
    Optional<Member> findById(String id);  //아이디로 조회
    Optional<Member> findByEmail(String email);
}
