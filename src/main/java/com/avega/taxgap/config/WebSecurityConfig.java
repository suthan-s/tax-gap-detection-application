package com.avega.taxgap.config;

import com.avega.taxgap.service.LoginUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class WebSecurityConfig implements HandlerInterceptor {

    private final LoginUtil loginUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("authorization");
        if (!(request.getRequestURI().contains("login") || request.getRequestURI().contains("sign-up"))){
            loginUtil.validate(token);
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

}
