package com.nagarro.candidatemanagement.tokenutils;

import com.nagarro.candidatemanagement.tokenutils.model.UserDetails;

public interface TokenManager {
    UserDetails getUserDetailsFromToken(String token);
}
