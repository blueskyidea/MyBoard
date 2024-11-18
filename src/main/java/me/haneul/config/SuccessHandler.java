package me.haneul.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import me.haneul.config.jwt.JwtTokenProvider;
import me.haneul.config.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import me.haneul.entity.Member;
import me.haneul.entity.RefreshToken;
import me.haneul.repository.RefreshTokenRepository;
import me.haneul.service.MemberService;
import me.haneul.util.CookieUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

/*일반 로그인 => 인증에 성공(로그인 성공) 시 실행*/
@RequiredArgsConstructor
@Component
public class SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofDays(1);

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // 일반 로그인 유저 처리
        Member member = (Member) authentication.getPrincipal();  // UserDetails로 반환된 Member

        //리프레시 토큰 생성 -> 저장 -> 쿠키에 저장
        String refreshToken = jwtTokenProvider.generateToken(member, REFRESH_TOKEN_DURATION);
        saveRefreshToken(member.getId(), refreshToken);
        addTokenToCookie(request, response, refreshToken, "refresh_token");

        //액세스 토큰 생성 -> 쿠키에 저장
        String accessToken = jwtTokenProvider.generateToken(member, ACCESS_TOKEN_DURATION);
        addTokenToCookie(request, response, accessToken, "access_token");

        //리다이렉트
        getRedirectStrategy().sendRedirect(request, response, "/select");
    }

    //토큰을 쿠키에 저장
    private void addTokenToCookie(HttpServletRequest request, HttpServletResponse response, String token, String name) {
        if(name.equals("access_token")) {
            int cookieMaxAge = (int) ACCESS_TOKEN_DURATION.toSeconds();
            CookieUtil.deleteCookie(request, response, name);
            CookieUtil.addCookie(response, name, token, cookieMaxAge);
        } else if(name.equals("refresh_token")) {
            int cookieMaxAge = (int) REFRESH_TOKEN_DURATION.toSeconds();
            CookieUtil.deleteCookie(request, response, name);
            CookieUtil.addCookie(response, name, token, cookieMaxAge);
        }
    }

    //생성된 리프레시 토큰을 전달받아 데이터베이스(Redis)에 저장
    private void saveRefreshToken(String memberId, String newRefreshToken) {
        RefreshToken refreshToken = new RefreshToken(memberId, newRefreshToken);

        refreshTokenRepository.save(refreshToken);
    }
}
