package com.nagarro.candidatemanagement.gateway.impl;

import com.nagarro.candidatemanagement.controller.dto.TokenDTO;
import com.nagarro.candidatemanagement.gateway.TokenValidator;
import com.nagarro.candidatemanagement.gateway.config.UserManagementConfigProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class TokenValidatorImpl implements TokenValidator {
    private final UserManagementConfigProperties configProperties;

    public TokenValidatorImpl(UserManagementConfigProperties configProperties) {
        this.configProperties = configProperties;
    }

    @Override
    public boolean verify(String token) {
        WebClient webClient = WebClient.create(configProperties.getBaseUrl());
        TokenDTO tokenDTO = new TokenDTO(token);

        HttpStatus status = webClient.post()
                .uri(configProperties.getTokenValidation())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(tokenDTO), TokenDTO.class)
                .exchangeToMono(clientResponse -> Mono.just(clientResponse.statusCode()))
                .block();

        return status == HttpStatus.NO_CONTENT;
    }
}
