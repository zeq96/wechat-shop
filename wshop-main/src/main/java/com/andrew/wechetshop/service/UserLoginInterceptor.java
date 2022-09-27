package com.andrew.wechetshop.service;

import org.apache.shiro.SecurityUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

public class UserLoginInterceptor implements HandlerInterceptor {
    private final UserService userService;

    public UserLoginInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Object principal = SecurityUtils.getSubject().getPrincipal();
        if (principal != null) {
            userService.selectUserByTel(principal.toString())
                    .ifPresent(UserContext::setCurrentUser);
        }

        if (isWhitelist(request)) {
            return true;
        } else if (UserContext.getCurrentUser() == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 请注意：此步骤非常重要 由于线程会被复用 必须在结束后必须清理上下文
        UserContext.clearCurrentUser();
    }

    private boolean isWhitelist(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return Arrays.asList(
                "/api/v1/code",
                "/api/v1/login",
                "/api/v1/status",
                "/api/v1/logout",
                "/error",
                "/",
                "/index.html"
        ).contains(uri) || uri.startsWith("/static/");
    }
}
