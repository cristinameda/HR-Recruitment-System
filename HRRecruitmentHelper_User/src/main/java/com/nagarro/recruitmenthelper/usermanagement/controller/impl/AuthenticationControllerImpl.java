package com.nagarro.recruitmenthelper.usermanagement.controller.impl;

import com.nagarro.recruitmenthelper.usermanagement.controller.AuthenticationController;
import com.nagarro.recruitmenthelper.usermanagement.controller.dto.AuthenticationRequestDTO;
import com.nagarro.recruitmenthelper.usermanagement.controller.dto.AuthenticationResponseDTO;
import com.nagarro.recruitmenthelper.usermanagement.controller.dto.TokenDTO;
import com.nagarro.recruitmenthelper.usermanagement.controller.dto.UserDTO;
import com.nagarro.recruitmenthelper.usermanagement.service.AuthenticationService;
import com.nagarro.recruitmenthelper.usermanagement.tokenutils.TokenManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationControllerImpl implements AuthenticationController {
    private final AuthenticationService authenticationService;
    private final TokenManager tokenManager;

    public AuthenticationControllerImpl(TokenManager tokenManager, AuthenticationService authenticationService) {
        this.tokenManager = tokenManager;
        this.authenticationService = authenticationService;
    }

    public ResponseEntity<AuthenticationResponseDTO> createToken(AuthenticationRequestDTO authenticationRequestDTO) {
        UserDTO userDTO = authenticationService.authenticate(authenticationRequestDTO);
        String token = tokenManager.generateToken(userDTO);
        return ResponseEntity.ok().body(new AuthenticationResponseDTO(tokenManager.getValidity(), token));
    }

    public ResponseEntity<Void> validateToken(TokenDTO token) {
        authenticationService.validateToken(token.getToken());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
