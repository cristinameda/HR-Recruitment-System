package com.nagarro.candidatemanagement.interceptor;

import com.nagarro.candidatemanagement.config.BearerTokenWrapper;
import com.nagarro.candidatemanagement.exception.HeaderAuthorizationException;
import com.nagarro.candidatemanagement.exception.TokenExpiredException;
import com.nagarro.candidatemanagement.gateway.TokenValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BearerTokenInterceptorTest {
    @Mock
    private BearerTokenWrapper bearerTokenWrapper;
    @Mock
    private TokenValidator tokenValidator;
    @InjectMocks
    private BearerTokenInterceptor bearerTokenInterceptor;

    @Test
    void preHandle_shouldReturnTrue() {
        String token = "Bearer valid_token";
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = MockMvcRequestBuilders.get("/candidates")
                .header("Authorization", token)
                .buildRequest(new MockServletContext());
        when(tokenValidator.verify(token.substring(7))).thenReturn(true);

        assertTrue(bearerTokenInterceptor.preHandle(request, response, new Object()));
    }

    @Test
    void preHandle_whenAuthorizationHeaderMissing_shouldThrowHeaderAuthorizationException() {
        MockHttpServletResponse response = new MockHttpServletResponse();

        MockHttpServletRequest request = MockMvcRequestBuilders.get("/candidates")
                .buildRequest(new MockServletContext());

        assertThrows(HeaderAuthorizationException.class, () -> bearerTokenInterceptor.preHandle(request, response, new Object()));
    }

    @Test
    void preHandle_whenBearerKeywordMissing_shouldThrowHeaderAuthorizationException() {
        String token = "not_valid_token";
        MockHttpServletResponse response = new MockHttpServletResponse();

        MockHttpServletRequest request = MockMvcRequestBuilders.get("/candidates")
                .header("Authorization", token)
                .buildRequest(new MockServletContext());

        assertThrows(HeaderAuthorizationException.class, () -> bearerTokenInterceptor.preHandle(request, response, new Object()));
    }

    @Test
    void preHandle_whenTokenIsInvalid_shouldThrowTokenExpiredException() {
        String token = "Bearer expired_token";
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = MockMvcRequestBuilders.get("/candidates")
                .header("Authorization", token)
                .buildRequest(new MockServletContext());
        when(tokenValidator.verify(token.substring(7))).thenReturn(false);

        assertThrows(TokenExpiredException.class, () -> bearerTokenInterceptor.preHandle(request, response, new Object()));
    }
}