package com.nagarro.recruitmenthelper.usermanagement.controller.impl;

import com.nagarro.recruitmenthelper.usermanagement.controller.UserController;
import com.nagarro.recruitmenthelper.usermanagement.controller.dto.UserDTO;
import com.nagarro.recruitmenthelper.usermanagement.controller.dto.UserEmailRoleDTO;
import com.nagarro.recruitmenthelper.usermanagement.controller.dto.ValidationResponse;
import com.nagarro.recruitmenthelper.usermanagement.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserControllerImpl implements UserController {

    private final UserService userService;

    public UserControllerImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public ResponseEntity<UserDTO> addUser(UserDTO userDTO) {
        return new ResponseEntity<>(userService.addUser(userDTO), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<UserDTO> findById(Long id) {
        return new ResponseEntity<>(userService.findById(id), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<UserDTO>> findAll(int page, int pageSize) {
        return new ResponseEntity<>(userService.findAll(page, pageSize), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> delete(long userId) {
        userService.delete(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<ValidationResponse> areUsersValid(List<UserEmailRoleDTO> userEmailRoleDTOS) {
        boolean response = userService.areUsersValid(userEmailRoleDTOS);
        return new ResponseEntity<>(new ValidationResponse(response), HttpStatus.OK);
    }
}
