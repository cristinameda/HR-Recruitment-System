package com.nagarro.recruitmenthelper.usermanagement.security.interceptors;

import com.nagarro.recruitmenthelper.usermanagement.controller.dto.BearerTokenWrapper;
import com.nagarro.recruitmenthelper.usermanagement.exception.HeaderAuthorizationException;
import com.nagarro.recruitmenthelper.usermanagement.service.AuthenticationService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class BearerTokenInterceptor implements HandlerInterceptor {
    private final BearerTokenWrapper bearerTokenWrapper;
    private final AuthenticationService authenticationService;

    public BearerTokenInterceptor(BearerTokenWrapper bearerTokenWrapper, AuthenticationService authenticationService) {
        this.bearerTokenWrapper = bearerTokenWrapper;
        this.authenticationService = authenticationService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        final String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new HeaderAuthorizationException("Authorization key is invalid or is not present!");
        }
        String jwtToken = authorizationHeader.substring(7);
        authenticationService.validateToken(jwtToken);
        bearerTokenWrapper.setToken(jwtToken);
        return true;
    }
}