package com.nagarro.recruitmenthelper.usermanagement.controller.dto;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class BearerTokenWrapper {
    private String token;

    public BearerTokenWrapper() {
    }

    public BearerTokenWrapper(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
