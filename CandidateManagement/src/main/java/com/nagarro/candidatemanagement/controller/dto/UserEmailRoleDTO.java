package com.nagarro.candidatemanagement.controller.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

public class UserEmailRoleDTO {
    @Email(regexp = "^([a-zA-Z]+[0-9]*[-_.]*)+[a-zA-Z]+[0-9]*" + // [email name]
            "@([a-zA-Z]+[0-9]*[.-]*)+[a-zA-Z]+[0-9]*" + // [app]
            "[.][a-zA-Z]{2,4}", // domain
            message = "Email is not valid!")
    private String email;

    @NotEmpty
    private String roleName;

    public UserEmailRoleDTO() {
    }

    public UserEmailRoleDTO(String email, String roleName) {
        this.email = email;
        this.roleName = roleName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}

