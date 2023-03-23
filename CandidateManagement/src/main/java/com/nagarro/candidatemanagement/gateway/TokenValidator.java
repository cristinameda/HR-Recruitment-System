package com.nagarro.candidatemanagement.gateway;

public interface TokenValidator {
    boolean verify(String token);
}
