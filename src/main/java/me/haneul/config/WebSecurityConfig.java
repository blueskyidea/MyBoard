//package me.haneul.config;
//
//import lombok.RequiredArgsConstructor;
//import me.haneul.config.jwt.JwtTokenProvider;
//import me.haneul.repository.RefreshTokenRepository;
//import me.haneul.service.MemberDetailService;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.annotation.Order;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.ProviderManager;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
//
//@Configuration
//@EnableWebSecurity
//@RequiredArgsConstructor
//@Order(1)
//public class WebSecurityConfig {
//    private final FailHandler failureHandler;
//    private final JwtTokenProvider jwtTokenProvider;  //Jwt를 사용할 것이므로 선언
//    private final RefreshTokenRepository refreshTokenRepository;
//
//    //스프링 시큐리티 기능 비활성화
//    @Bean
//    public WebSecurityCustomizer configure() {
//        return (web) -> web.ignoring()
//                .requestMatchers("/h2-console/**")  //h2 데이터를 확인하는데 사용하는 h2-console 하위 url 대상 인증, 인가 비활성화
//                .requestMatchers(new AntPathRequestMatcher("/img/**"))  // 정적 리소스(img 폴더) 인증, 인가 비활성화
//                .requestMatchers(new AntPathRequestMatcher("/static/**"));  //정적 리소스 인증, 인가 비활성화
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        //토큰 방식으로 인증을 하기 때문에 기존 사용하던 폼 로그인, 세션 비활성화
//        return http
//                .csrf(AbstractHttpConfigurer::disable)  //csrf 비활성화
//                .httpBasic(AbstractHttpConfigurer::disable)
//                .formLogin(AbstractHttpConfigurer::disable)
//                .logout(AbstractHttpConfigurer::disable)
//                //스프링 시큐리티가 세션을 생성하지도 않고 기존것을 사용하지도 않음(JWT 방식 사용하기 위해 하는 설정)
//                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                //헤더를 확인할 커스텀 필터 추가(=TokenAuthenticationFilter 클래스)
//                .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
//                .authorizeRequests(auth -> auth  //인증, 인가 설정
//                        .requestMatchers(  //특정 요청과 일치하는 url에 대한 액세스 설정
//                                new AntPathRequestMatcher("/member/loginPage"),
//                                new AntPathRequestMatcher("/member/login"),
//                                new AntPathRequestMatcher("/member/joinPage"),
//                                new AntPathRequestMatcher("/member/join"),
//                                new AntPathRequestMatcher("/select"),
//                                new AntPathRequestMatcher("/select/**"),
//                                new AntPathRequestMatcher("/comment/select"),
//                                new AntPathRequestMatcher("/member/oauth2loginPage")
//                        ).permitAll()  //누구나 접근이 가능하게 설정
//                        //anyRequest(): 위에서 설정한 url 이외의 요청에 대해 설정,
//                        //authenticated(): 별도의 인가는 필요하지 않지만 인증이 성공된 상태여야 접근 가능
//                        .anyRequest().authenticated()
//                )
//                .formLogin(formLogin -> formLogin  //폼 기반 로그인 설정
//                        .loginPage("/member/loginPage")  //로그인 페이지 경로 설정
//                        .loginProcessingUrl("/login")    // 로그인 요청 경로 설정
//                        .usernameParameter("username")
//                        .passwordParameter("password")
//                        .successHandler(successHandler())
//                        .failureHandler(failureHandler)  // 실패 핸들러 등록
//                )
//                .logout(logout -> logout  //로그아웃 설정
//                        .logoutSuccessUrl("/member/loginPage")  //로그아웃이 완료되었을 때 이동할 경로 설정
//                        .invalidateHttpSession(true)  //로그아웃 이후에 세션을 전체 삭제할지 여부 설정
//                )
//                .build();
//    }
//
////    @Bean
////    public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder, MemberDetailService memberDetailService) throws Exception {
////        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
////        authProvider.setUserDetailsService(memberDetailService);
////        authProvider.setPasswordEncoder(bCryptPasswordEncoder);
////        return new ProviderManager(authProvider);
////    }
//
//    //헤더 확인
//    @Bean
//    public TokenAuthenticationFilter tokenAuthenticationFilter() {
//        return new TokenAuthenticationFilter(jwtTokenProvider);
//    }
//
//    @Bean
//    public SuccessHandler successHandler() {
//        return new SuccessHandler(jwtTokenProvider, refreshTokenRepository);
//    }
//}
