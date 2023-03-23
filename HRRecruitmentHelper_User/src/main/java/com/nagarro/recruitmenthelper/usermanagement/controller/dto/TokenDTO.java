package com.nagarro.recruitmenthelper.usermanagement.controller.dto;

import javax.validation.constraints.NotBlank;

public class TokenDTO {
    @NotBlank(message = "Token is mandatory!")
    private String token;

    public TokenDTO() {

    }

    public TokenDTO(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
