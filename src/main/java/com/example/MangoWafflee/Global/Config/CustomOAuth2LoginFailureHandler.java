package com.example.MangoWafflee.Global.Config;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomOAuth2LoginFailureHandler implements AuthenticationFailureHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2LoginFailureHandler.class);

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        logger.error("OAuth2 로그인 실패: {}", exception.getMessage());

        response.sendRedirect("/loginFailure"); // 실패 시 리다이렉트할 URL
    }
}
