package me.haneul.config.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.haneul.entity.Member;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final JwtProperties jwtProperties;

    public String generateToken(Member member, Duration expiredAt) {
        Date now = new Date();
        return createToken(new Date(now.getTime() + expiredAt.toMillis()), member, UserRoleEnum.USER);
    }

    //토큰 생성
    public String createToken(Date expiry, Member member, UserRoleEnum role) {
        Date now = new Date();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)  //헤더 type: JWT
                //iss(토큰 발급자): gksmf3199@naver.com(properties(yml)에서 설정한 값)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(now)  //iat(토큰이 발급된 시간): 현재 시간
                .setExpiration(expiry)  //exp(토큰 만료 시간): expiry 멤버 변수값
                .setSubject(member.getId())
                .claim("email", member.getEmail())  //클레임 email: 유저의 이메일
                .claim("role", role) //클레임 role: 유저의 권한(운영자인지 아닌지)
                //서명: 비밀값과 함께 해시값을 HS256 방식으로 암호화
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    //토큰 유효성을 검증하는 메서드
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSignKey()) //비밀값으로 복호화
                    .build().parseClaimsJws(token);

            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token, 유효하지 않은 JWT Token 입니다.", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token, 만료된 JWT Token 입니다.", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token, 지원되지 않는 JWT Token 입니다.", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty, 잘못된 JWT Token 입니다.", e);
        }
        return false;
    }

    //토큰에서 사용자 정보 가져오기
    public Authentication getUserInfoFromToken(String token) {
        Claims claims = getClaims(token);
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(
                new SimpleGrantedAuthority(claims.get("role").toString()));

//        Member member = new Member();  // Member 객체 생성
//        member.setId(claims.get("id").toString());  // 사용자 ID를 별도로 클레임에서 가져오기
//        member.setEmail(claims.getSubject());  // sub에서 이메일 가져오기
//
//        return new UsernamePasswordAuthenticationToken(member, token, authorities);  // Member 객체를 Principal로 설정

        return new UsernamePasswordAuthenticationToken(new User(claims.getSubject(), "", authorities),
                token, authorities);
    }

    //토큰 기반으로 유저 ID를 가져오는 메서드
    public Long getUserId(String token) {
        Claims claims = getClaims(token);
        return claims.get("id", Long.class);
    }

    //토큰 기반으로 유저 ID를 가져오는 메서드
    public Long getUserEmail(String token) {
        Claims claims = getClaims(token);
        return claims.get("email", Long.class);
    }

    //클레임 조회
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build().parseClaimsJws(token)
                .getBody();
    }

    //secretKey 가져오기
    private Key getSignKey() {
        String secretKey = jwtProperties.getSecretKey();
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
