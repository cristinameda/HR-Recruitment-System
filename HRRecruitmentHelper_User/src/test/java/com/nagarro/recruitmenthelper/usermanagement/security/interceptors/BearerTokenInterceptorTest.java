package com.nagarro.recruitmenthelper.usermanagement.security.interceptors;

import com.nagarro.recruitmenthelper.usermanagement.controller.dto.BearerTokenWrapper;
import com.nagarro.recruitmenthelper.usermanagement.exception.HeaderAuthorizationException;
import com.nagarro.recruitmenthelper.usermanagement.service.AuthenticationService;
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

@ExtendWith(MockitoExtension.class)
public class BearerTokenInterceptorTest {
    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private BearerTokenWrapper bearerTokenWrapper;
    @InjectMocks
    private BearerTokenInterceptor underTestBearerTokenInterceptor;

    @Test
    void testPreHandle_shouldReturnTrue() {
        String validToken = "Bearer validToken";
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = MockMvcRequestBuilders.get("/users")
                .header("Authorization", validToken)
                .buildRequest(new MockServletContext());
        assertTrue(underTestBearerTokenInterceptor.preHandle(request, response, new Object()));
    }

    @Test
    void testPreHandle_shouldThrowHeaderAuthorizationException() {
        String validToken = "invalidToken";
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = MockMvcRequestBuilders.get("/users")
                .header("Authorization", validToken)
                .buildRequest(new MockServletContext());
        assertThrows(HeaderAuthorizationException.class, () -> underTestBearerTokenInterceptor.preHandle(request, response, new Object()));
    }
}
