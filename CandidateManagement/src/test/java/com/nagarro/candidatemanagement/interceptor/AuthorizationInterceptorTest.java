package com.nagarro.candidatemanagement.interceptor;

import com.nagarro.candidatemanagement.config.BearerTokenWrapper;
import com.nagarro.candidatemanagement.exception.HeaderAuthorizationException;
import com.nagarro.candidatemanagement.tokenutils.impl.JwtTokenManager;
import com.nagarro.candidatemanagement.tokenutils.model.UserDetails;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
class AuthorizationInterceptorTest {
    private final static String TOKEN_ADMIN_REQUEST = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMUB5YWhvby5jb20iLCJyb2xlIjoiQWRtaW4iLCJleHAiOjE2NTkxMjA0OTMsImlhdCI6MTY1OTExOTg5M30.HW_lkdz1QnUClQ1CI0I_AOmOwhP8m2xzZS26ZGHGHITKeuS__sw1hTayUgDeZjh0YounYLlUFq3RJ4LmId7wsQ";
    private final static String TOKEN_HR_REQUEST = "Bearer OiJIUzUxMiJ9.eyJzdWIiOiJzdHJpbmcuc3RyaW5nQHlhaG9vLmNvbSIsInJvbGUiOiJIclJlcHJlc2VudGF0aXZlIiwiZXhwIjoxNjU5MTIwODM4LCJpYXQiOjE2NTkxMjAyMzh9.HG1jmVxuDv0eTXFV7KvgoJFAWZGN7j59VsYxEWZlP0MK6h0CWECf8yYqi0uCN7c-O3H6xAfVgN6SrH7cV-SM9A";
    private final static String TOKEN_TI_REQUEST = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMkB5YWhvby5jb20iLCJyb2xlIjoiVGVobmljYWxJbnRlcnZpZXdlciIsImV4cCI6MTY1OTEyMTE2OSwiaWF0IjoxNjU5MTIwNTY5fQ.mTypjd0qcdKMYhcl3kKkU9B_xn9WXxs9k-t-x_2GDe070j_fq8e86eLgsnfxXFH4-mshYZh1EiH58kICZsO7RQ";
    private final static String TOKEN_PTE_REQUEST = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyM0B5YWhvby5jb20iLCJyb2xlIjoiUFRFIiwiZXhwIjoxNjU5MTIxMzM1LCJpYXQiOjE2NTkxMjA3MzV9.5Nt0EEr4tbRfrl5o8xEJBqf0BtlLl4bICLUmmZ5ETYvPNlJ_iYG_A9STz2P2YxcGlc2f4jfx004LeeXyyf5RnQ";

    @Mock
    private JwtTokenManager jwtTokenManager;
    @Mock
    private BearerTokenWrapper bearerTokenWrapper;
    @InjectMocks
    private AuthorizationInterceptor authorizationInterceptor;

    @Test
    void preHandle_adminRequestGetShouldThrowUnauthorized() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        UserDetails userDetails = new UserDetails("admin@yahoo.com", "Admin");
        MockHttpServletRequest request = MockMvcRequestBuilders.get("/candidates")
                .header("Authorization", TOKEN_ADMIN_REQUEST)
                .buildRequest(new MockServletContext());
        when(bearerTokenWrapper.getToken()).thenReturn(TOKEN_ADMIN_REQUEST);
        when(jwtTokenManager.getUserDetailsFromToken(TOKEN_ADMIN_REQUEST)).thenReturn(userDetails);

        assertThrows(HeaderAuthorizationException.class, () -> authorizationInterceptor.preHandle(request, response, new Object()));
    }

    @Test
    void preHandle_adminRequestPostShouldThrowUnauthorized() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        UserDetails userDetails = new UserDetails("admin@yahoo.com", "Admin");
        MockHttpServletRequest request = MockMvcRequestBuilders.post("/candidates")
                .header("Authorization", TOKEN_ADMIN_REQUEST)
                .buildRequest(new MockServletContext());
        when(bearerTokenWrapper.getToken()).thenReturn(TOKEN_ADMIN_REQUEST);
        when(jwtTokenManager.getUserDetailsFromToken(TOKEN_ADMIN_REQUEST)).thenReturn(userDetails);

        assertThrows(HeaderAuthorizationException.class, () -> authorizationInterceptor.preHandle(request, response, new Object()));
    }

    @Test
    void preHandle_adminRequestDeleteShouldReturnTrue() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        UserDetails userDetails = new UserDetails("admin@yahoo.com", "Admin");
        MockHttpServletRequest request = MockMvcRequestBuilders.delete("/candidates/2")
                .header("Authorization", TOKEN_ADMIN_REQUEST)
                .buildRequest(new MockServletContext());
        when(bearerTokenWrapper.getToken()).thenReturn(TOKEN_ADMIN_REQUEST);
        when(jwtTokenManager.getUserDetailsFromToken(TOKEN_ADMIN_REQUEST)).thenReturn(userDetails);

        assertTrue(authorizationInterceptor.preHandle(request, response, new Object()));
    }

    @Test
    void preHandle_hrRequestGetShouldReturnTrue() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        UserDetails userDetails = new UserDetails("hrRepresentative@yahoo.com", "HrRepresentative");
        MockHttpServletRequest request = MockMvcRequestBuilders.get("/candidates")
                .header("Authorization", TOKEN_HR_REQUEST)
                .buildRequest(new MockServletContext());
        when(bearerTokenWrapper.getToken()).thenReturn(TOKEN_HR_REQUEST);
        when(jwtTokenManager.getUserDetailsFromToken(TOKEN_HR_REQUEST)).thenReturn(userDetails);

        assertTrue(authorizationInterceptor.preHandle(request, response, new Object()));
    }

    @Test
    void preHandle_hrRequestPostShouldReturnTrue() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        UserDetails userDetails = new UserDetails("hrRepresentative@yahoo.com", "HrRepresentative");
        MockHttpServletRequest request = MockMvcRequestBuilders.post("/candidates")
                .header("Authorization", TOKEN_HR_REQUEST)
                .buildRequest(new MockServletContext());
        when(bearerTokenWrapper.getToken()).thenReturn(TOKEN_HR_REQUEST);
        when(jwtTokenManager.getUserDetailsFromToken(TOKEN_HR_REQUEST)).thenReturn(userDetails);

        assertTrue(authorizationInterceptor.preHandle(request, response, new Object()));
    }

    @Test
    void preHandle_hrRequestPutShouldReturnTrue() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        UserDetails userDetails = new UserDetails("hrRepresentative@yahoo.com", "HrRepresentative");
        MockHttpServletRequest request = MockMvcRequestBuilders.put("/candidates/status")
                .header("Authorization", TOKEN_HR_REQUEST)
                .buildRequest(new MockServletContext());
        when(bearerTokenWrapper.getToken()).thenReturn(TOKEN_HR_REQUEST);
        when(jwtTokenManager.getUserDetailsFromToken(TOKEN_HR_REQUEST)).thenReturn(userDetails);

        assertTrue(authorizationInterceptor.preHandle(request, response, new Object()));
    }

    @Test
    void preHandle_hrRequestDeleteShouldThrowUnauthorized() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        UserDetails userDetails = new UserDetails("hrRepresentative@yahoo.com", "HrRepresentative");
        MockHttpServletRequest request = MockMvcRequestBuilders.delete("/candidates/2")
                .header("Authorization", TOKEN_HR_REQUEST)
                .buildRequest(new MockServletContext());
        when(bearerTokenWrapper.getToken()).thenReturn(TOKEN_HR_REQUEST);
        when(jwtTokenManager.getUserDetailsFromToken(TOKEN_HR_REQUEST)).thenReturn(userDetails);

        assertThrows(HeaderAuthorizationException.class, () -> authorizationInterceptor.preHandle(request, response, new Object()));
    }

    @Test
    void preHandle_technicalInterviewerRequestGetShouldReturnTrue() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        UserDetails userDetails = new UserDetails("technicalInterviewer@yahoo.com", "TechnicalInterviewer");

        MockHttpServletRequest request = MockMvcRequestBuilders.get("/candidates")
                .header("Authorization", TOKEN_TI_REQUEST)
                .buildRequest(new MockServletContext());
        when(bearerTokenWrapper.getToken()).thenReturn(TOKEN_TI_REQUEST);
        when(jwtTokenManager.getUserDetailsFromToken(TOKEN_TI_REQUEST)).thenReturn(userDetails);

        assertTrue(authorizationInterceptor.preHandle(request, response, new Object()));
    }

    @Test
    void preHandle_technicalInterviewerRequestPostShouldThrowUnauthorized() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        UserDetails userDetails = new UserDetails("technicalInterviewer@yahoo.com", "TechnicalInterviewer");
        MockHttpServletRequest request = MockMvcRequestBuilders.post("/candidates")
                .header("Authorization", TOKEN_TI_REQUEST)
                .buildRequest(new MockServletContext());
        when(bearerTokenWrapper.getToken()).thenReturn(TOKEN_TI_REQUEST);
        when(jwtTokenManager.getUserDetailsFromToken(TOKEN_TI_REQUEST)).thenReturn(userDetails);

        assertThrows(HeaderAuthorizationException.class, () -> authorizationInterceptor.preHandle(request, response, new Object()));
    }

    @Test
    void preHandle_technicalInterviewerRequestDeleteShouldThrowUnauthorized() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        UserDetails userDetails = new UserDetails("technicalInterviewer@yahoo.com", "TechnicalInterviewer");
        MockHttpServletRequest request = MockMvcRequestBuilders.delete("/candidates/2")
                .header("Authorization", TOKEN_TI_REQUEST)
                .buildRequest(new MockServletContext());
        when(bearerTokenWrapper.getToken()).thenReturn(TOKEN_TI_REQUEST);
        when(jwtTokenManager.getUserDetailsFromToken(TOKEN_TI_REQUEST)).thenReturn(userDetails);

        assertThrows(HeaderAuthorizationException.class, () -> authorizationInterceptor.preHandle(request, response, new Object()));
    }

    @Test
    void preHandle_technicalInterviewerRequestPutShouldThrowUnauthorized() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        UserDetails userDetails = new UserDetails("technicalInterviewer@yahoo.com", "TechnicalInterviewer");
        MockHttpServletRequest request = MockMvcRequestBuilders.put("/candidates/status")
                .header("Authorization", TOKEN_TI_REQUEST)
                .buildRequest(new MockServletContext());
        when(bearerTokenWrapper.getToken()).thenReturn(TOKEN_TI_REQUEST);
        when(jwtTokenManager.getUserDetailsFromToken(TOKEN_TI_REQUEST)).thenReturn(userDetails);

        assertThrows(HeaderAuthorizationException.class, () -> authorizationInterceptor.preHandle(request, response, new Object()));
    }

    @Test
    void preHandle_pteRequestGetShouldReturnTrue() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        UserDetails userDetails = new UserDetails("pte@yahoo.com", "PTE");
        MockHttpServletRequest request = MockMvcRequestBuilders.get("/candidates")
                .header("Authorization", TOKEN_PTE_REQUEST)
                .buildRequest(new MockServletContext());
        when(bearerTokenWrapper.getToken()).thenReturn(TOKEN_PTE_REQUEST);
        when(jwtTokenManager.getUserDetailsFromToken(TOKEN_PTE_REQUEST)).thenReturn(userDetails);

        assertTrue(authorizationInterceptor.preHandle(request, response, new Object()));
    }

    @Test
    void preHandle_pteRequestPostShouldThrowUnauthorized() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        UserDetails userDetails = new UserDetails("pte@yahoo.com", "PTE");
        MockHttpServletRequest request = MockMvcRequestBuilders.post("/candidates")
                .header("Authorization", TOKEN_PTE_REQUEST)
                .buildRequest(new MockServletContext());
        when(bearerTokenWrapper.getToken()).thenReturn(TOKEN_PTE_REQUEST);
        when(jwtTokenManager.getUserDetailsFromToken(TOKEN_PTE_REQUEST)).thenReturn(userDetails);

        assertThrows(HeaderAuthorizationException.class, () -> authorizationInterceptor.preHandle(request, response, new Object()));
    }

    @Test
    void preHandle_pteRequestDeleteShouldThrowUnauthorized() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        UserDetails userDetails = new UserDetails("pte@yahoo.com", "PTE");
        MockHttpServletRequest request = MockMvcRequestBuilders.delete("/candidates/2")
                .header("Authorization", TOKEN_PTE_REQUEST)
                .buildRequest(new MockServletContext());
        when(bearerTokenWrapper.getToken()).thenReturn(TOKEN_PTE_REQUEST);
        when(jwtTokenManager.getUserDetailsFromToken(TOKEN_PTE_REQUEST)).thenReturn(userDetails);

        assertThrows(HeaderAuthorizationException.class, () -> authorizationInterceptor.preHandle(request, response, new Object()));
    }

    @Test
    void preHandle_pteRequestPutShouldReturnTrue() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        UserDetails userDetails = new UserDetails("pte@yahoo.com", "PTE");
        MockHttpServletRequest request = MockMvcRequestBuilders.put("/candidates/status")
                .header("Authorization", TOKEN_PTE_REQUEST)
                .buildRequest(new MockServletContext());
        when(bearerTokenWrapper.getToken()).thenReturn(TOKEN_PTE_REQUEST);
        when(jwtTokenManager.getUserDetailsFromToken(TOKEN_PTE_REQUEST)).thenReturn(userDetails);

        assertTrue(authorizationInterceptor.preHandle(request, response, new Object()));
    }

    @ParameterizedTest
    @ValueSource(strings = {TOKEN_HR_REQUEST, TOKEN_PTE_REQUEST, TOKEN_TI_REQUEST})
    void preHandle_feedbackPutRequestShouldReturnTrue() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        UserDetails userDetails = new UserDetails("pte@yahoo.com", "PTE");
        MockHttpServletRequest request = MockMvcRequestBuilders.put("/candidates/feedback/1")
                .header("Authorization", TOKEN_PTE_REQUEST)
                .buildRequest(new MockServletContext());
        when(bearerTokenWrapper.getToken()).thenReturn(TOKEN_PTE_REQUEST);
        when(jwtTokenManager.getUserDetailsFromToken(TOKEN_PTE_REQUEST)).thenReturn(userDetails);

        assertTrue(authorizationInterceptor.preHandle(request, response, new Object()));
    }

    @Test
    void preHandle_whenAdminFeedbackPutRequest_shouldThrowHeaderAuthorizationException() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        UserDetails userDetails = new UserDetails("admin@yahoo.com", "Admin");
        MockHttpServletRequest request = MockMvcRequestBuilders.put("/candidates/feedback/1")
                .header("Authorization", TOKEN_ADMIN_REQUEST)
                .buildRequest(new MockServletContext());
        when(bearerTokenWrapper.getToken()).thenReturn(TOKEN_ADMIN_REQUEST);
        when(jwtTokenManager.getUserDetailsFromToken(TOKEN_ADMIN_REQUEST)).thenReturn(userDetails);

        assertThrows(HeaderAuthorizationException.class, () ->
                authorizationInterceptor.preHandle(request, response, new Object()));
    }
}