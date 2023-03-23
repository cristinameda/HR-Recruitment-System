package com.nagarro.recruitmenthelper.usermanagement.tokenutils;

import com.nagarro.recruitmenthelper.usermanagement.controller.dto.UserDTO;

public interface TokenManager {
    String generateToken(UserDTO userDTO);

    Long getValidity();

    String getUsernameFromToken(String token);

    Boolean isTokenExpired(String token);
}
