package com.nagarro.candidatemanagement.tokenutils.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserDetails {
    @JsonProperty("sub")
    private String email;
    private String role;

    public UserDetails() {
    }

    public UserDetails(String email, String role) {
        this.email = email;
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
