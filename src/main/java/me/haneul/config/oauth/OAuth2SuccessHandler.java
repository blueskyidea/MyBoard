package me.haneul.config.oauth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import me.haneul.config.jwt.JwtTokenProvider;
import me.haneul.entity.Member;
import me.haneul.entity.RefreshToken;
import me.haneul.repository.RefreshTokenRepository;
import me.haneul.service.MemberService;
import me.haneul.util.CookieUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

/*OAuth2 로그인 => 인증에 성공(로그인 성공) 시 실행*/
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofDays(1);

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final me.haneul.config.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository auth2AuthorizationRequestBasedOnCookieRepository;
    private final MemberService memberService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                       Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        Member member;

        //카카오(카카오의 경우 이메일은 kakao_account 객체 내부에 존재)
        if(oAuth2User.getAttributes().containsKey("kakao_account")) {
            String email = (String) ((Map<?, ?>) oAuth2User.getAttributes().get("kakao_account")).get("email");
            member = memberService.findByEmail(email);
        }
        else {  //구글
            member = memberService.findByEmail((String) oAuth2User.getAttributes().get("email"));
        }

        //리프레시 토큰 생성 -> 저장 -> 쿠키에 저장
        String refreshToken = jwtTokenProvider.generateToken(member, REFRESH_TOKEN_DURATION);
        saveRefreshToken(member.getId(), refreshToken);
        addTokenToCookie(request, response, refreshToken, "refresh_token");

        //액세스 토큰 생성 -> 쿠키에 저장
        String accessToken = jwtTokenProvider.generateToken(member, ACCESS_TOKEN_DURATION);
        addTokenToCookie(request, response, accessToken, "access_token");
        //인증 관련 설정값, 쿠키 제거(인증 프로세스를 진행하면서 세션과 쿠키에 임시로 저장해둔 인증 관련 데이터 제거)
        clearAuthenticationAttributes(request, response);
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

//    //생성된 리프레시 토큰을 쿠키에 저장
//    private void addRefreshTokenToCookie(HttpServletRequest request, HttpServletResponse response, String refreshToken) {
//        int cookieMaxAge = (int) REFRESH_TOKEN_DURATION.toSeconds();
//        CookieUtil.deleteCookie(request, response, "refresh_token");
//        CookieUtil.addCookie(response, "refresh_token", refreshToken, cookieMaxAge);
//    }

    //인증 관련 설정값, 쿠키 제거
    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        auth2AuthorizationRequestBasedOnCookieRepository.removeAuthorizationRequestCookies(request, response);
    }
}
