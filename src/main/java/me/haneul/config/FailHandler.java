package me.haneul.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.security.SecurityConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;

@Component
public class FailHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        // 로그인 실패 시, 오류 메시지를 모델에 추가해서 로그인 페이지로 돌아가게 할 수 있습니다.
        String errorMessage = "아이디 또는 비밀번호가 틀렸습니다.";

        // 로그인 페이지로 리다이렉트
        request.setAttribute("errorMessage", errorMessage);
        response.sendRedirect("/member/loginPage?error=" + URLEncoder.encode(errorMessage, "UTF-8"));
    }
}
