package com.nagarro.candidatemanagement.tokenutils.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nagarro.candidatemanagement.exception.HeaderAuthorizationException;
import com.nagarro.candidatemanagement.tokenutils.TokenManager;
import com.nagarro.candidatemanagement.tokenutils.model.UserDetails;
import java.util.Base64;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenManager implements TokenManager {
    private final ObjectMapper mapper;

    public JwtTokenManager(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public UserDetails getUserDetailsFromToken(String token) {
        return getUserDetails(convertToPayload(token));
    }

    private String convertToPayload(String token) {
        Base64.Decoder decoder = java.util.Base64.getUrlDecoder();
        String[] parts = token.split("\\.");
        return new String(decoder.decode(parts[1]));
    }

    private UserDetails getUserDetails(String decodedPayload) {
        try {
            return mapper.readValue(decodedPayload, UserDetails.class);
        } catch (JsonProcessingException e) {
            throw new HeaderAuthorizationException("Invalid token!");
        }
    }
}
