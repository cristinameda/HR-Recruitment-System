package com.nagarro.recruitmenthelper.usermanagement.controller.impl;

import com.nagarro.recruitmenthelper.usermanagement.controller.dto.AuthenticationRequestDTO;
import com.nagarro.recruitmenthelper.usermanagement.controller.dto.AuthenticationResponseDTO;
import com.nagarro.recruitmenthelper.usermanagement.controller.dto.UserDTO;
import com.nagarro.recruitmenthelper.usermanagement.service.AuthenticationService;
import com.nagarro.recruitmenthelper.usermanagement.tokenutils.TokenManager;
import com.nagarro.recruitmenthelper.usermanagement.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthenticationControllerImplTest {
    @Mock
    private TokenManager tokenManager;
    @Mock
    private AuthenticationService authenticationService;
    @InjectMocks
    private AuthenticationControllerImpl authenticationController;

    @Test
    void testCreateToken() {
        UserDTO userDTO = TestUtils.buildUserDTO(1);
        AuthenticationRequestDTO authenticationRequestDTO = new AuthenticationRequestDTO(userDTO.getEmail(), userDTO.getPassword());
        when(authenticationService.authenticate(authenticationRequestDTO)).thenReturn(userDTO);
        when(tokenManager.generateToken(userDTO)).thenReturn("token");
        when(tokenManager.getValidity()).thenReturn(100L);
        AuthenticationResponseDTO responseDTO = new AuthenticationResponseDTO(100L, "token");
        ResponseEntity<AuthenticationResponseDTO> expectedResponseEntity = ResponseEntity.ok().body(responseDTO);

        ResponseEntity<AuthenticationResponseDTO> actualResponseEntity = authenticationController.createToken(authenticationRequestDTO);

        assertEquals(expectedResponseEntity.getStatusCode(), actualResponseEntity.getStatusCode());
        assertNotNull(expectedResponseEntity.getBody());
        assertNotNull(actualResponseEntity.getBody());
        assertEquals(expectedResponseEntity.getBody().getToken(), actualResponseEntity.getBody().getToken());
        assertEquals(expectedResponseEntity.getBody().getValidity(), actualResponseEntity.getBody().getValidity());
    }
}
