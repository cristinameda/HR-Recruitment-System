package com.nagarro.candidatemanagement.interceptor;

import com.nagarro.candidatemanagement.config.BearerTokenWrapper;
import com.nagarro.candidatemanagement.exception.HeaderAuthorizationException;
import com.nagarro.candidatemanagement.tokenutils.TokenManager;
import com.nagarro.candidatemanagement.tokenutils.model.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthorizationInterceptor implements HandlerInterceptor {
    private final static String ADMIN_ROLE = "Admin";
    private final static String HR_REPRESENTATIVE_ROLE = "HrRepresentative";
    private final static String PTE_ROLE = "PTE";
    private final static String TECHNICAL_INTERVIEWER_ROLE = "TechnicalInterviewer";
    private final BearerTokenWrapper tokenWrapper;
    private final TokenManager tokenManager;

    public AuthorizationInterceptor(BearerTokenWrapper tokenWrapper, TokenManager tokenManager) {
        this.tokenWrapper = tokenWrapper;
        this.tokenManager = tokenManager;
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        UserDetails userDetails = tokenManager.getUserDetailsFromToken(tokenWrapper.getToken());
        String role = userDetails.getRole();

        if (request.getMethod().equals("GET") && hasGetEligibleRoles(role)) {
            throw new HeaderAuthorizationException("You are not authorized to access this resource!");
        }

        if (request.getMethod().equals("POST") &&
                request.getRequestURI().equals("/candidates") &&
                hasPostCandidatesEligibleRoles(role)) {
            throw new HeaderAuthorizationException("You are not authorized to access this resource!");
        }
        if (request.getMethod().equals("POST") &&
                request.getRequestURI().equals("/interview") &&
                hasPostInterviewEligibleRoles(role)) {
            throw new HeaderAuthorizationException("You are not authorized to access this resource!");
        }
        if (request.getMethod().equals("POST") &&
                request.getRequestURI().matches("/feedback(/.+)?")
                && hasPostFeedbackEligibleRoles(role)) {
            throw new HeaderAuthorizationException("You are not authorized to access this resource!");
        }

        if (request.getMethod().equals("DELETE") && hasDeleteEligibleRoles(role)) {
            throw new HeaderAuthorizationException("You are not authorized to access this resource!");
        }
        if (request.getMethod().equals("PUT") && hasPutEligibleRoles(role)) {
            throw new HeaderAuthorizationException("You are not authorized to access this resource!");
        }
        return true;
    }

    private boolean hasGetEligibleRoles(String role) {
        return !(role.equals(HR_REPRESENTATIVE_ROLE) || role.equals(PTE_ROLE) || role.equals(TECHNICAL_INTERVIEWER_ROLE));
    }

    private boolean hasPostFeedbackEligibleRoles(String role) {
        return !(role.equals(HR_REPRESENTATIVE_ROLE) || role.equals(PTE_ROLE) || role.equals(TECHNICAL_INTERVIEWER_ROLE));
    }

    private boolean hasPostCandidatesEligibleRoles(String role) {
        return !role.equals(HR_REPRESENTATIVE_ROLE);
    }

    private boolean hasPostInterviewEligibleRoles(String role) {
        return !role.equals(HR_REPRESENTATIVE_ROLE);
    }

    private boolean hasDeleteEligibleRoles(String role) {
        return !role.equals(ADMIN_ROLE);
    }

    private boolean hasPutEligibleRoles(String role) {
        return !(role.equals(HR_REPRESENTATIVE_ROLE) || role.equals(PTE_ROLE));
    }
}
