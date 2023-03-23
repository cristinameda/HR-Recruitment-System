package com.nagarro.recruitmenthelper.usermanagement.service.impl;

import com.nagarro.recruitmenthelper.usermanagement.controller.dto.AuthenticationRequestDTO;
import com.nagarro.recruitmenthelper.usermanagement.controller.dto.UserDTO;
import com.nagarro.recruitmenthelper.usermanagement.domain.User;
import com.nagarro.recruitmenthelper.usermanagement.exception.InvalidCredentialsException;
import com.nagarro.recruitmenthelper.usermanagement.exception.InvalidTokenException;
import com.nagarro.recruitmenthelper.usermanagement.exception.UserNotFoundException;
import com.nagarro.recruitmenthelper.usermanagement.repository.UserRepository;
import com.nagarro.recruitmenthelper.usermanagement.service.AuthenticationService;
import com.nagarro.recruitmenthelper.usermanagement.service.security.PasswordHashGenerator;
import com.nagarro.recruitmenthelper.usermanagement.tokenutils.TokenManager;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;


@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final TokenManager tokenManager;
    private final PasswordHashGenerator passwordHashGenerator;

    public AuthenticationServiceImpl(UserRepository userRepository, ModelMapper modelMapper, TokenManager tokenManager, PasswordHashGenerator passwordHashGenerator) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.tokenManager = tokenManager;
        this.passwordHashGenerator = passwordHashGenerator;
    }

    @Override
    public UserDTO authenticate(AuthenticationRequestDTO authenticationRequestDTO) {
        Optional<User> requestedUser = userRepository.findUserByEmail(authenticationRequestDTO.getEmail());
        if (requestedUser.isEmpty()) {
            throw new UserNotFoundException("User with email: " + authenticationRequestDTO.getEmail() + " not found!");
        }
        return requestedUser
                .filter(user -> passwordHashGenerator.matches(authenticationRequestDTO.getPassword(), user.getPassword()))
                .map(user -> modelMapper.map(user, UserDTO.class))
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials!"));
    }

    @Override
    public void validateToken(String token) {
        try {
            tokenManager.isTokenExpired(token);
        } catch (MalformedJwtException e) {
            throw new InvalidTokenException("Token is malformed!");
        } catch (ExpiredJwtException e) {
            throw new InvalidTokenException("The token has expired");
        } catch (SignatureException e) {
            throw new InvalidTokenException("Invalid signature!");
        }

        userRepository.findUserByEmail(tokenManager.getUsernameFromToken(token))
                .orElseThrow(() -> new InvalidTokenException("Token invalid! User not found!"));
    }
}
