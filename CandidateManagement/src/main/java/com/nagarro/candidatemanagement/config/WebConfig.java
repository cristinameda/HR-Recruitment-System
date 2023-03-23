package com.nagarro.candidatemanagement.config;

import com.nagarro.candidatemanagement.interceptor.AuthorizationInterceptor;
import com.nagarro.candidatemanagement.interceptor.BearerTokenInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebMvc
@Configuration
public class WebConfig implements WebMvcConfigurer {
    private static final String[] WHITE_LIST = {"/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**"};

    private final BearerTokenInterceptor bearerTokenInterceptor;
    private final AuthorizationInterceptor authorizationInterceptor;

    public WebConfig(BearerTokenInterceptor bearerTokenInterceptor, AuthorizationInterceptor authorizationInterceptor) {
        this.bearerTokenInterceptor = bearerTokenInterceptor;
        this.authorizationInterceptor = authorizationInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(bearerTokenInterceptor).excludePathPatterns(WHITE_LIST);
        registry.addInterceptor(authorizationInterceptor).excludePathPatterns(WHITE_LIST);
    }
}
