package com.nagarro.recruitmenthelper.usermanagement.controller;

import com.nagarro.recruitmenthelper.usermanagement.controller.dto.AuthenticationRequestDTO;
import com.nagarro.recruitmenthelper.usermanagement.controller.dto.AuthenticationResponseDTO;
import com.nagarro.recruitmenthelper.usermanagement.controller.dto.TokenDTO;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface AuthenticationController {

    @PostMapping("/login")
    @ApiOperation(value = "Generates token for the given user if given credentials are correct")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token returned!"),
    })
    ResponseEntity<AuthenticationResponseDTO> createToken(@RequestBody AuthenticationRequestDTO authenticationRequestDTO);

    @PostMapping("/validateToken")
    @ApiOperation(value = "Validates token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token is valid!"),
    })
    ResponseEntity<Void> validateToken(@RequestBody TokenDTO token);
}
