package com.nagarro.recruitmenthelper.usermanagement.controller;

import com.nagarro.recruitmenthelper.usermanagement.controller.dto.UserDTO;
import com.nagarro.recruitmenthelper.usermanagement.controller.dto.UserEmailRoleDTO;
import com.nagarro.recruitmenthelper.usermanagement.controller.dto.ValidationResponse;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("/users")
public interface UserController {

    @PostMapping
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiOperation(value = "Add a new user", notes = "Returns the user created.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User was successfully created!"),
            @ApiResponse(responseCode = "400", description = "Bad request!")
    })
    ResponseEntity<UserDTO> addUser(@ApiParam(
            name = "user",
            type = "User",
            value = "Object of type User",
            required = true) @Valid @RequestBody UserDTO userDTO);

    @GetMapping("/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiOperation(value = "Find user by id", notes = "Returns the user with the given id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User was found and returned!"),
            @ApiResponse(responseCode = "404", description = "No user was found with that id!")
    })
    ResponseEntity<UserDTO> findById(@PathVariable Long id);

    @GetMapping
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiOperation(value = "Find all users", notes = "Returns user list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User list returned!"),
    })
    ResponseEntity<List<UserDTO>> findAll(@RequestParam(required = false, defaultValue = "1") int page,
                                          @RequestParam(required = false, defaultValue = "15") int pageSize);

    @DeleteMapping("/{userId}")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiOperation(value = "Deletes an existing user by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User was successfully deleted!"),
            @ApiResponse(responseCode = "404", description = "User not found!")
    })
    ResponseEntity<Void> delete(@PathVariable long userId);

    @PostMapping("/validation")
    @ApiOperation(value = "Check if users exist")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Emails and roles successfully checked! All users exist!"),
            @ApiResponse(responseCode = "400", description = "Bad request!")
    })
    ResponseEntity<ValidationResponse> areUsersValid(@RequestBody List<@Valid UserEmailRoleDTO> userEmailRoleDTOS);
}
