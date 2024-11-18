package me.haneul.config;

import me.haneul.config.jwt.JwtTokenProvider;
import me.haneul.config.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import me.haneul.config.oauth.OAuth2SuccessHandler;
import me.haneul.config.oauth.OAuth2UserCustomService;
import me.haneul.repository.RefreshTokenRepository;
import me.haneul.service.MemberService;
import lombok.RequiredArgsConstructor;
import me.haneul.service.UserDetailService;
import org.apache.catalina.security.SecurityConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

/* OAuth2와 JWT를 함께 사용하기 위한 설정 */
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class WebOAuthSecurityConfig {
    private final OAuth2UserCustomService oAuth2UserCustomService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberService memberService;
    private final FailHandler failureHandler;

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean //(name = "WebOAuthSecurityCustomizer")
    public WebSecurityCustomizer configure() {  //스프링 시큐리티 기능 비활성화
        return (web) -> web.ignoring()
                .requestMatchers("/h2-console/**")
                //.requestMatchers(toH2Console())
                .requestMatchers(
                        new AntPathRequestMatcher("/images/**"),
                        new AntPathRequestMatcher("/css/**"),
                        new AntPathRequestMatcher("/js/**")
                );

    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //토큰 방식으로 인증을 하기 때문에 기존 사용하던 폼 로그인, 세션 비활성화
        return http
                .csrf(AbstractHttpConfigurer::disable)  //csrf 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                //스프링 시큐리티가 세션을 생성하지도 않고 기존것을 사용하지도 않음(JWT 방식 사용하기 위해 하는 설정)
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //헤더를 확인할 커스텀 필터 추가(=TokenAuthenticationFilter 클래스)
                .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                //토큰 재발급 URL은 인증 없이 접근 가능하도록 설정. 나머지 API URL은 인증 필요
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(new AntPathRequestMatcher("/api/token")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/**")).authenticated()
                        .requestMatchers(new AntPathRequestMatcher("/select")).permitAll()  //게시판 페이지
                        .requestMatchers(new AntPathRequestMatcher("/select/Id/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/member/loginPage")).permitAll()  //로그인 페이지
                        .requestMatchers(new AntPathRequestMatcher("/member/oauth2loginPage")).permitAll()
                        .requestMatchers(HttpMethod.POST, "/member/join").permitAll()  //회원가입 페이지
                        .requestMatchers(HttpMethod.GET, "/member/joinPage").permitAll()
                        .anyRequest().authenticated()  //그 외 모든 요청은 인증 필요
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/member/loginPage")  //로그인 페이지(oauth2) 설정
                        //Authorization 요청과 관련된 상태 저장(OAuth2에 필요한 정보를 세션이 아닌 쿠키에 저장할 수 있도록 설정)
                        .authorizationEndpoint(authorizationEndpoint -> authorizationEndpoint
                                .authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository()))
                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint.userService(oAuth2UserCustomService))
                        //인증 성공 시 실행할 핸들러
                        .successHandler(oAuth2SuccessHandler())
                )
                .formLogin(formLogin -> formLogin  //폼 기반 로그인 설정
                        .loginPage("/member/loginPage")  //로그인 페이지 경로 설정
                        .loginProcessingUrl("/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .successHandler(successHandler())
                        .failureHandler(failureHandler)  // 실패 핸들러 등록
                )
                .logout(logout -> logout  //로그아웃 설정
                        .logoutSuccessUrl("/member/loginPage")  //로그아웃이 완료되었을 때 이동할 경로 설정
                        .invalidateHttpSession(true)  //로그아웃 이후에 세션을 전체 삭제할지 여부 설정
                )
                // /api로 시작하는 url인 경우 인증 실패 시 401 상태 코드를 반환하도록 예외 처리
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .defaultAuthenticationEntryPointFor(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                                new AntPathRequestMatcher("/api/**")
                        ))
                .build();

    }

    //사용자 인증 처리
    @Bean
    public AuthenticationManager authenticationManager(BCryptPasswordEncoder bCryptPasswordEncoder, UserDetailService userDetailService) throws Exception {
        //데이터베이스에서 사용자 정보 가져옴
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        //사용자 정보 가져옴
        authProvider.setUserDetailsService(userDetailService);
        //비밀번호 비교
        authProvider.setPasswordEncoder(bCryptPasswordEncoder);
        return new ProviderManager(authProvider);
    }

    //인증 성공 시 실행할 핸들러
    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler() {
        return new OAuth2SuccessHandler(jwtTokenProvider, refreshTokenRepository,
                oAuth2AuthorizationRequestBasedOnCookieRepository(), memberService);
    }

    //헤더 확인
    @Bean //(name = "OAuthTokenAuthenticationFilter")
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter(jwtTokenProvider);
    }

    //OAuth2에 필요한 정보 쿠키에 저장
    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }

    @Bean
    public SuccessHandler successHandler() {
        return new SuccessHandler(jwtTokenProvider, refreshTokenRepository);
    }
}
