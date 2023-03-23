package com.nagarro.candidatemanagement.gateway.impl;

import com.nagarro.candidatemanagement.controller.dto.UserEmailRoleDTO;
import com.nagarro.candidatemanagement.controller.dto.ValidationResponse;
import com.nagarro.candidatemanagement.exception.UserApiIntegrationException;
import com.nagarro.candidatemanagement.gateway.UsersValidator;
import com.nagarro.candidatemanagement.gateway.config.UserManagementConfigProperties;
import java.util.Collections;
import java.util.List;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class UsersValidatorImpl implements UsersValidator {
    private final RestTemplate restTemplate;
    private final UserManagementConfigProperties configProperties;

    public UsersValidatorImpl(RestTemplate restTemplate, UserManagementConfigProperties configProperties) {
        this.restTemplate = restTemplate;
        this.configProperties = configProperties;
    }

    @Override
    public boolean areUsersValid(List<UserEmailRoleDTO> users) {
        HttpEntity<List<UserEmailRoleDTO>> request = new HttpEntity<>(users);
        try {
            ResponseEntity<ValidationResponse> response = restTemplate.postForEntity(
                    configProperties.getBaseUrl() + configProperties.getUserValidation(), request, ValidationResponse.class);
            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                throw new UserApiIntegrationException("User api responded with '" + response.getStatusCode() + "'!");
            }
            return response.getBody().getValid();
        } catch (HttpClientErrorException exception) {
            throw new UserApiIntegrationException("", exception);
        }
    }

    @Override
    public boolean areRequestedRolesValid(List<UserEmailRoleDTO> users) {
        if (users.size() < 4 || users.size() > 5) {
            return false;
        }
        List<String> roles = users.stream()
                .map(UserEmailRoleDTO::getRoleName)
                .toList();

        return Collections.frequency(roles, "HrRepresentative") == 2 &&
                Collections.frequency(roles, "PTE") == 1 &&
                (Collections.frequency(roles, "TechnicalInterviewer") == 1 ||
                        Collections.frequency(roles, "TechnicalInterviewer") == 2);
    }
}
