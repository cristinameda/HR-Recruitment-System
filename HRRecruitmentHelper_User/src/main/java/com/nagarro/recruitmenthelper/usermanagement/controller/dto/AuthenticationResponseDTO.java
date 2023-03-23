package com.nagarro.recruitmenthelper.usermanagement.controller.dto;

public class AuthenticationResponseDTO {
    private String token;
    private Long validity;

    public AuthenticationResponseDTO() {
    }

    public AuthenticationResponseDTO(Long validity, String token) {
        this.validity = validity;
        this.token = token;
    }

    public Long getValidity() {
        return validity;
    }

    public void setValidity(Long validity) {
        this.validity = validity;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
