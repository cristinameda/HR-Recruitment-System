package com.nagarro.candidatemanagement.tokenutils.impl;

import com.nagarro.candidatemanagement.tokenutils.TokenManager;
import com.nagarro.candidatemanagement.tokenutils.model.UserDetails;
import com.nagarro.candidatemanagement.utils.TestDataBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class JwtTokenManagerTest {

    @Autowired
    TokenManager jwtTokenManager;

    @Test
    void getUserDetailsFromToken() {
        UserDetails expectedUserDetails = TestDataBuilder.buildUserDetails("test@yahoo.com", "HrRepresentative");
        UserDetails actualUserDetails =
                jwtTokenManager.getUserDetailsFromToken(TestDataBuilder.buildHrRepresentativeToken());

        assertEquals(expectedUserDetails.getRole(), actualUserDetails.getRole());
        assertEquals(expectedUserDetails.getEmail(), actualUserDetails.getEmail());
    }
}