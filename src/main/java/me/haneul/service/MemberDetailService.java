package me.haneul.service;

import lombok.RequiredArgsConstructor;
import me.haneul.entity.Member;
import me.haneul.repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
//UserDetailsService: 스프링 시큐리티에서 사용자 정보를 가져오는 인터페이스
public class MemberDetailService implements UserDetailsService {
    private final MemberRepository memberRepository;

    //사용자 이름(email)으로 사용자 정보를 가져오는 메서드
    @Override
    public Member loadUserByUsername(String email) {
        return memberRepository.findById(email)
                .orElseThrow(() -> new IllegalArgumentException(email));
    }
}
