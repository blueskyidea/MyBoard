package me.haneul.service;

import lombok.RequiredArgsConstructor;
import me.haneul.config.jwt.JwtTokenProvider;
import me.haneul.entity.Member;
import org.springframework.stereotype.Service;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class TokenService {
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final MemberService memberService;

    public String createNewAccessToken(String refreshToken) {
        //토큰 유효성 검사에 실패하면 예외 발생
        if(!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Unexpected token");
        }

        Long userId = Long.valueOf(refreshTokenService.findByRefreshToken(refreshToken).getId());
        Member member = memberService.findById(userId);

        return jwtTokenProvider.generateToken(member, Duration.ofHours(2));
    }
}
