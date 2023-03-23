package com.nagarro.recruitmenthelper.usermanagement.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nagarro.recruitmenthelper.usermanagement.validationstrategy.FieldValidationStrategy;
import com.nagarro.recruitmenthelper.usermanagement.validationstrategy.Unique;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Objects;
import java.util.StringJoiner;

public class UserDTO {
    private Long id;
    @NotBlank(message = "Name is mandatory!")
    private String name;
    @NotEmpty(message = "Email is mandatory!")
    @Email(message = "Email is not valid!")
    @Unique(strategy = FieldValidationStrategy.class, fieldName = "email", message = "This email was already used!")
    private String email;
    @NotNull(message = "Password is mandatory!")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,24}$",
            message = "The password must be at least 8 and at most 24 characters long and contain at least 1 upper case, 1 lower case, 1 special character!")
    private String password;
    @JsonProperty("role")
    @NotNull
    private RoleDTO roleDTO;

    public UserDTO() {

    }

    public UserDTO(Long id, String name, String email, String password, RoleDTO roleDTO) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.roleDTO = roleDTO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public RoleDTO getRoleDTO() {
        return roleDTO;
    }

    public void setRoleDTO(RoleDTO roleDTO) {
        this.roleDTO = roleDTO;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, password);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", UserDTO.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .add("email='" + email + "'")
                .add("password='" + password + "'")
                .add("roleDTO=" + roleDTO)
                .toString();
    }
}
