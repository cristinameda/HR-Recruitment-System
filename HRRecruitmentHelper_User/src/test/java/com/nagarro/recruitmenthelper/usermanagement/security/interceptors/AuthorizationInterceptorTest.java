package com.nagarro.recruitmenthelper.usermanagement.security.interceptors;

import com.nagarro.recruitmenthelper.usermanagement.controller.dto.BearerTokenWrapper;
import com.nagarro.recruitmenthelper.usermanagement.exception.AuthorizationException;
import com.nagarro.recruitmenthelper.usermanagement.service.UserService;
import com.nagarro.recruitmenthelper.usermanagement.tokenutils.TokenManager;
import com.nagarro.recruitmenthelper.usermanagement.utils.TestUtils;
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
public class AuthorizationInterceptorTest {
    @Mock
    private BearerTokenWrapper bearerTokenWrapper;
    @Mock
    private UserService userService;
    @Mock
    private TokenManager tokenManager;
    @InjectMocks
    private AuthorizationInterceptor underTestAuthorizationInterceptor;

    @Test
    void testPreHandle_shouldReturnTrue() {
        when(bearerTokenWrapper.getToken()).thenReturn("token");
        when(tokenManager.getUsernameFromToken("token")).thenReturn("email");
        when(userService.findUserByEmail("email")).thenReturn(TestUtils.buildUser());
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = MockMvcRequestBuilders.post("/users")
                .header("Authorization", "token")
                .buildRequest(new MockServletContext());

        assertTrue(underTestAuthorizationInterceptor.preHandle(request, response, new Object()));
    }

    @Test
    void testPreHandle_shouldThrowAuthorisationExceptionOnPost() {
        when(bearerTokenWrapper.getToken()).thenReturn("token");
        when(tokenManager.getUsernameFromToken("token")).thenReturn("email");
        when(userService.findUserByEmail("email")).thenReturn(TestUtils.buildUser(1));
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = MockMvcRequestBuilders.post("/users")
                .header("Authorization", "token")
                .buildRequest(new MockServletContext());

        assertThrows(AuthorizationException.class, () -> underTestAuthorizationInterceptor.preHandle(request, response, new Object()));
    }

    @Test
    void testPreHandle_shouldThrowAuthorisationExceptionOnDelete() {
        when(bearerTokenWrapper.getToken()).thenReturn("token");
        when(tokenManager.getUsernameFromToken("token")).thenReturn("email");
        when(userService.findUserByEmail("email")).thenReturn(TestUtils.buildUser(1));
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = MockMvcRequestBuilders.delete("/users/1")
                .header("Authorization", "token")
                .buildRequest(new MockServletContext());

        assertThrows(AuthorizationException.class, () -> underTestAuthorizationInterceptor.preHandle(request, response, new Object()));
    }
}
