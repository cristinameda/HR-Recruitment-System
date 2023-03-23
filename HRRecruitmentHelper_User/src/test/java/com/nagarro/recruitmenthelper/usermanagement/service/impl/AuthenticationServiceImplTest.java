package com.nagarro.recruitmenthelper.usermanagement.service.impl;

import com.nagarro.recruitmenthelper.usermanagement.controller.dto.AuthenticationRequestDTO;
import com.nagarro.recruitmenthelper.usermanagement.controller.dto.UserDTO;
import com.nagarro.recruitmenthelper.usermanagement.domain.User;
import com.nagarro.recruitmenthelper.usermanagement.exception.InvalidCredentialsException;
import com.nagarro.recruitmenthelper.usermanagement.exception.InvalidTokenException;
import com.nagarro.recruitmenthelper.usermanagement.exception.UserNotFoundException;
import com.nagarro.recruitmenthelper.usermanagement.repository.UserRepository;
import com.nagarro.recruitmenthelper.usermanagement.service.security.PasswordHashGenerator;
import com.nagarro.recruitmenthelper.usermanagement.tokenutils.TokenManager;
import com.nagarro.recruitmenthelper.usermanagement.utils.TestUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private TokenManager tokenManager;
    @Mock
    private PasswordHashGenerator passwordHashGenerator;
    @InjectMocks
    private AuthenticationServiceImpl userDetailsService;

    @Test
    void testAuthenticate_shouldReturnUserDTO() {
        User user = TestUtils.buildUser(1);
        UserDTO userDTO = TestUtils.buildUserDTO(1);
        AuthenticationRequestDTO authenticationRequestDTO = new AuthenticationRequestDTO(userDTO.getEmail(), userDTO.getPassword());
        when(userRepository.findUserByEmail(authenticationRequestDTO.getEmail())).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserDTO.class)).thenReturn(userDTO);
        when(passwordHashGenerator.matches(authenticationRequestDTO.getPassword(), user.getPassword())).thenReturn(true);

        UserDTO actualUserDTO = userDetailsService.authenticate(authenticationRequestDTO);

        assertEquals(userDTO, actualUserDTO);
    }

    @Test
    void testAuthenticate_whenUserDoesntExist_shouldThrowUserNotFoundException() {
        User user = TestUtils.buildUser(1);
        AuthenticationRequestDTO authenticationRequestDTO = new AuthenticationRequestDTO(user.getEmail(), user.getPassword());
        when(userRepository.findUserByEmail(authenticationRequestDTO.getEmail())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userDetailsService.authenticate(authenticationRequestDTO));
    }

    @Test
    void testAuthenticate_whenPasswordNotMatching_shouldThrowInvalidCredentialsException() {
        User user = TestUtils.buildUser(1);
        UserDTO userDTO = TestUtils.buildUserDTO(1);
        AuthenticationRequestDTO authenticationRequestDTO =
                new AuthenticationRequestDTO(userDTO.getEmail(), "paSsword123!");
        when(userRepository.findUserByEmail(authenticationRequestDTO.getEmail())).thenReturn(Optional.of(user));

        assertThrows(InvalidCredentialsException.class, () -> userDetailsService.authenticate(authenticationRequestDTO));
    }

    @Test
    void testValidateToken_validToken() {
        String validToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJwb2pqcGFAeWFob28uY29tIiwicm9sZSI6IkFkbWluIiwiZXhwIjoxNjU4ODM3ODI2LCJpYXQiOjE2NTg4MzcyMjZ9.G_3Ayo5O1H-Z8XeONQ4f-8mrWFYSJpboEYqm-R4Wj0ylqHLZY-7sBikyPUU3UXWbPBt1ouT08Q7i2G8kHqweOw";

        when(tokenManager.getUsernameFromToken(validToken)).thenReturn("pojjpa@yahoo.com");
        when(userRepository.findUserByEmail("pojjpa@yahoo.com")).thenReturn(Optional.of(new User()));

        userDetailsService.validateToken(validToken);
    }

    @Test
    void testValidateToken_tokenIsExpired() {
        String invalidToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJwb2pqcGFAeWFob28uY29tIiwicm9sZSI6IkFkbWluIiwiZXhwIjoxNjU4ODM3ODI2LCJpYXQiOjE2NTg4MzcyMjZ9.G_3Ayo5O1H-Z8XeONQ4f-8mrWFYSJpboEYqm-R4Wj0ylqHLZY-7sBikyPUU3UXWbPBt1ouT08Q7i2G8kHqweOw";

        when(tokenManager.isTokenExpired(invalidToken)).thenThrow(ExpiredJwtException.class);

        assertThrows(InvalidTokenException.class, () -> userDetailsService.validateToken(invalidToken));
    }

    @Test
    void testValidateToken_tokenIsMalformed() {
        String invalidToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJwb2pqcGFAeWFob28uY29tIiwicm9sZSI6IkFkbWluIiwiZXhwIjoxNjU4ODM3ODI2LCJpYXQiOjE2NTg4MzcyMjZ9.G_3Ayo5O1H-Z8XeONQ4f-8mrWFYSJpboEYqm-R4Wj0ylqHLZY-7sBikyPUU3UXWbPBt1ouT08Q7i2G8kHqweOw";

        when(tokenManager.isTokenExpired(invalidToken)).thenThrow(MalformedJwtException.class);

        assertThrows(InvalidTokenException.class, () -> userDetailsService.validateToken(invalidToken));
    }

    @Test
    void testValidateToken_tokenHasSignatureChanged() {
        String invalidToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJwb2pqcGFAeWFob28uY29tIiwicm9sZSI6IkFkbWluIiwiZXhwIjoxNjU4ODM3ODI2LCJpYXQiOjE2NTg4MzcyMjZ9.G_3Ayo5O1H-Z8XeONQ4f-8mrWFYSJpboEYqm-R4Wj0ylqHLZY-7sBikyPUU3UXWbPBt1ouT08Q7i2G8kHqweOw";

        when(tokenManager.isTokenExpired(invalidToken)).thenThrow(SignatureException.class);

        assertThrows(InvalidTokenException.class, () -> userDetailsService.validateToken(invalidToken));
    }

    @Test
    void testValidateToken_userNameNotFound() {
        String invalidToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJwb2pqcGFAeWFob28uY29tIiwicm9sZSI6IkFkbWluIiwiZXhwIjoxNjU4ODM3ODI2LCJpYXQiOjE2NTg4MzcyMjZ9.G_3Ayo5O1H-Z8XeONQ4f-8mrWFYSJpboEYqm-R4Wj0ylqHLZY-7sBikyPUU3UXWbPBt1ouT08Q7i2G8kHqweOw";

        when(tokenManager.getUsernameFromToken(invalidToken)).thenReturn("pojjpa@yahoo.com");
        when(userRepository.findUserByEmail("pojjpa@yahoo.com")).thenReturn(Optional.empty());

        assertThrows(InvalidTokenException.class, () -> userDetailsService.validateToken(invalidToken));
    }
}