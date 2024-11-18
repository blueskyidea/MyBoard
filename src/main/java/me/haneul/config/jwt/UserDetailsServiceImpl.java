package me.haneul.config.jwt;

import lombok.RequiredArgsConstructor;
import me.haneul.entity.Member;
import me.haneul.repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findById(username).orElseThrow(()
                -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));  //사용자가 DB에 없으면 예외처리

        return new UserDetailsImpl(member, member.getId());  //사용자 정보를 UserDetails로 반환
    }
}
