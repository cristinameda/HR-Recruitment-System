package com.nagarro.recruitmenthelper.usermanagement.controller.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

public class UserEmailRoleDTO {
    @Email(message = "Email is not valid!")
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
