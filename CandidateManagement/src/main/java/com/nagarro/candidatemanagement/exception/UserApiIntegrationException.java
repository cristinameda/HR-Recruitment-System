package com.nagarro.candidatemanagement.exception;

public class UserApiIntegrationException extends RuntimeException {
    public UserApiIntegrationException(String message) {
        super(message);
    }

    public UserApiIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
