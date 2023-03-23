package com.nagarro.candidatemanagement.interceptor;

import com.nagarro.candidatemanagement.config.BearerTokenWrapper;
import com.nagarro.candidatemanagement.exception.HeaderAuthorizationException;
import com.nagarro.candidatemanagement.exception.TokenExpiredException;
import com.nagarro.candidatemanagement.gateway.TokenValidator;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class BearerTokenInterceptor implements HandlerInterceptor {
    private final BearerTokenWrapper bearerTokenWrapper;
    private final TokenValidator tokenValidationGateway;

    public BearerTokenInterceptor(BearerTokenWrapper bearerTokenWrapper, TokenValidator tokenValidationGateway) {
        this.bearerTokenWrapper = bearerTokenWrapper;
        this.tokenValidationGateway = tokenValidationGateway;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new HeaderAuthorizationException("Invalid authorization key!");
        }
        String token = authorizationHeader.substring(7);
        if (!tokenValidationGateway.verify(token)) {
            throw new TokenExpiredException("Invalid Token");
        }
        bearerTokenWrapper.setToken(token);
        return true;
    }
}
