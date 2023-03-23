package com.nagarro.recruitmenthelper.usermanagement.security.config;

import com.nagarro.recruitmenthelper.usermanagement.security.interceptors.AuthorizationInterceptor;
import com.nagarro.recruitmenthelper.usermanagement.security.interceptors.BearerTokenInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebMvc
@Configuration
public class SecurityConfigurer implements WebMvcConfigurer {
    private final AuthorizationInterceptor authorizationInterceptor;
    private final BearerTokenInterceptor bearerTokenInterceptor;
    private static final String[] WHITE_LIST = {"/login", "/validateToken", "/users/validation","/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**"};

    public SecurityConfigurer(BearerTokenInterceptor bearerTokenInterceptor, AuthorizationInterceptor authorizationInterceptor) {
        this.bearerTokenInterceptor = bearerTokenInterceptor;
        this.authorizationInterceptor = authorizationInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(bearerTokenInterceptor).excludePathPatterns(WHITE_LIST);
        registry.addInterceptor(authorizationInterceptor).excludePathPatterns(WHITE_LIST);
    }
}
