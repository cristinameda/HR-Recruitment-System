package com.nagarro.candidatemanagement.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationProperties(prefix = "user-management")
@ConfigurationPropertiesScan
public class UserManagementConfigProperties {
    private String baseUrl;
    private String tokenValidation;
    private String userValidation;

    public UserManagementConfigProperties() {
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getTokenValidation() {
        return tokenValidation;
    }

    public void setTokenValidation(String tokenValidation) {
        this.tokenValidation = tokenValidation;
    }

    public String getUserValidation() {
        return userValidation;
    }

    public void setUserValidation(String userValidation) {
        this.userValidation = userValidation;
    }
}
