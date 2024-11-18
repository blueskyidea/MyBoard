//package me.haneul.config;
//
////클라이언트 요청 시 JWT 인증을 하기 위해 설치하는 커스텀 필터.
////UsernamePasswordAuthenticationFilter 이전에 실행.
////클라이언트로부터 들어오는 요청에서 JWT 토큰 처리,
////유효한 토큰인 경우 해당 토큰의 인증 정보를 SecurityContext에 저장하여 인증된 요청 처리.
//
//import io.jsonwebtoken.Claims;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import me.haneul.config.jwt.JwtTokenProvider;
//import me.haneul.dto.JwtDTO;
//import me.haneul.entity.Member;
//import me.haneul.entity.RefreshToken;
//import me.haneul.repository.MemberRepository;
//import me.haneul.repository.RefreshTokenRepository;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.Optional;
//
//@Slf4j
//@RequiredArgsConstructor
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//    private final JwtTokenProvider jwtTokenProvider;
//    private final RefreshTokenRepository refreshTokenRepository;
//    private final MemberRepository memberRepository;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
//                                    FilterChain chain) throws IOException, ServletException {
//        //1. Request Header에서 JWT(Access) 토큰 추출
//        String accesstoken = jwtTokenProvider.resolveAccessToken(request);
//        String refreshtoken = jwtTokenProvider.resolveRefreshToken(request);
//
//        //2. validateToken으로 토큰 유효성 검사
//        //Access 토큰이 null이거나 토큰이 유효하지 않으면 RefreshToken 유효성 검사
//        if(accesstoken == null || !jwtTokenProvider.validateToken(accesstoken)) {
//            //refreshToken 유효성 검사
//            //refreshToken이 유효하지 않으면 다음 필터로 넘어감(다시 로그인 필요)
//            //경고 메시지 출력 구현하기!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!(다시 로그인 해야하는 것과 단순 accesstoken 만료된것 구분할 수 있게)
//            if(refreshtoken == null || !jwtTokenProvider.validateToken(refreshtoken)) {
//                chain.doFilter(request, response);
//                return;
//            }
//            else {  //refreshToken이 유효하면 DB에 저장되어있는 refreshToken과 일치하는지 확인
//                String id = jwtTokenProvider.getUserInfoFromToken(refreshtoken).getSubject();
//                RefreshToken findrefreshToken = refreshTokenRepository.findById(id)
//                        .orElseThrow(() -> new IllegalArgumentException("RefreshToken이 존재하지 않습니다."));
//
//                if(!findrefreshToken.getRefreshToken().equals(refreshtoken)) {
//                    throw new IllegalArgumentException("DB의 RefreshToken과 일치하지 않습니다.");
//                }
//
//                //RefreshToken이 유효하다면 AccessToken 재발급
//                Optional<Member> member = memberRepository.findById(id);
//                JwtDTO jwt = jwtTokenProvider.recreationAccessToken(id, member.get().getRole(), refreshtoken);
//
//                // header 에 들어갈 JWT 세팅
//                response.setHeader("AccessHeader", jwt.getAccessToken());
//                response.setHeader("RefreshHeader", jwt.getRefreshToken());
//
//                //사용자 정보로 인증 객체 만들기
//                Claims info = jwtTokenProvider.getUserInfoFromToken(jwt.getAccessToken());
//                try {
//                    setAuthentication(info.getSubject());
//                } catch (UsernameNotFoundException e) {
//                    request.setAttribute("exception", "사용자를 찾을 수 없습니다.");
//                }
//            }
//            //다음 필터로 넘어감
//            chain.doFilter(request, response);
//            return;
//        }
//
//        //토큰이 유효할 경우 토큰으로부터 사용자 정보 가져옴
//        Claims info = jwtTokenProvider.getUserInfoFromToken(accesstoken);
//        try {
//            setAuthentication(info.getSubject());  //사용자 정보로 인증 객체 만들기
//        } catch (UsernameNotFoundException e) {
//            request.setAttribute("exception", "사용자를 찾을 수 없습니다.");
//        }
//
//        chain.doFilter(request, response);  //다음 필터로 넘어감
//    }
//
//    private void setAuthentication(String username) {
//        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
//        Authentication authentication = jwtTokenProvider.createAuthentication(username);  //인증 객체 만들기
//        securityContext.setAuthentication(authentication);
//
//        SecurityContextHolder.setContext(securityContext);
//    }
//}
//
