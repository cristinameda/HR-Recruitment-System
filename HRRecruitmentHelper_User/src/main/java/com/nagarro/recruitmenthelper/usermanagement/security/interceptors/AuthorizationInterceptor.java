package com.nagarro.recruitmenthelper.usermanagement.security.interceptors;

import com.nagarro.recruitmenthelper.usermanagement.controller.dto.BearerTokenWrapper;
import com.nagarro.recruitmenthelper.usermanagement.domain.User;
import com.nagarro.recruitmenthelper.usermanagement.exception.AuthorizationException;
import com.nagarro.recruitmenthelper.usermanagement.service.UserService;
import com.nagarro.recruitmenthelper.usermanagement.tokenutils.TokenManager;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthorizationInterceptor implements HandlerInterceptor {
    private static final String ADMIN_ROLE = "Admin";
    private final TokenManager tokenManager;
    private final UserService userService;
    private final BearerTokenWrapper bearerTokenWrapper;

    public AuthorizationInterceptor(TokenManager tokenManager, UserService userService, BearerTokenWrapper bearerTokenWrapper) {
        this.tokenManager = tokenManager;
        this.userService = userService;
        this.bearerTokenWrapper = bearerTokenWrapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String jwtToken = bearerTokenWrapper.getToken();
        User user = userService.findUserByEmail(tokenManager.getUsernameFromToken(jwtToken));
        if (request.getMethod().equals("POST") || request.getMethod().equals("DELETE")) {
            if (!ADMIN_ROLE.equals(user.getRole().getName())) {
                throw new AuthorizationException("You are not authorized to access this resource!");
            }
        }
        return true;
    }
}
