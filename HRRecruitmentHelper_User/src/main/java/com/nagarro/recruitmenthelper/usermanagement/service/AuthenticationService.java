package com.nagarro.recruitmenthelper.usermanagement.service;

import com.nagarro.recruitmenthelper.usermanagement.controller.dto.AuthenticationRequestDTO;
import com.nagarro.recruitmenthelper.usermanagement.controller.dto.UserDTO;

public interface AuthenticationService {
    UserDTO authenticate(AuthenticationRequestDTO authenticationRequestDTO);

    void validateToken(String token);
}
