package me.haneul.service;

import lombok.RequiredArgsConstructor;
import me.haneul.entity.Member;
import me.haneul.repository.MemberRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserDetailService implements UserDetailsService {
    private final MemberRepository memberRepository;

//    @Override
//    public Member loadUserByUsername(String Id) {
//        return memberRepository.findById(Id)
//                .orElseThrow(() -> new IllegalArgumentException(Id));
//    }

//    @Override
//    public UserDetails loadUserByUsername(String username) {
//        Member member = memberRepository.findById(username).orElseThrow(()
//                -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));  //사용자가 DB에 없으면 예외처리
//        return member;  //사용자 정보를 UserDetails로 반환
//    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // username이 email일 경우 Member를 가져옴
        Member member = memberRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        return member;  // Member 객체를 그대로 반환
    }
}
