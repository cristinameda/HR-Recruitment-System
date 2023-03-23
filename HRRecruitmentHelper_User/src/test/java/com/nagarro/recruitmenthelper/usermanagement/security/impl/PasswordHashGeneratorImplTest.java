package com.nagarro.recruitmenthelper.usermanagement.security.impl;

import com.nagarro.recruitmenthelper.usermanagement.service.security.PasswordHashGenerator;
import com.nagarro.recruitmenthelper.usermanagement.service.security.impl.PasswordHashGeneratorImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PasswordHashGeneratorImplTest {

    private final PasswordHashGenerator passwordHashGenerator = new PasswordHashGeneratorImpl();

    @Test
    public void testHashPassword() {
        String clearPassword = "Password123!";
        String expectedHash = "39c6192a47294be98ddc177c9a185a169342168c4308bf0cfc35ef5075d3b9a3";

        String actualHash = passwordHashGenerator.hashPassword(clearPassword);

        assertEquals(expectedHash, actualHash);
    }

    @Test
    public void testPasswordMatchesHash() {
        String clearPassword = "Password123!";
        String expectedHash = "39c6192a47294be98ddc177c9a185a169342168c4308bf0cfc35ef5075d3b9a3";

        boolean isMatching = passwordHashGenerator.matches(clearPassword, expectedHash);

        assertTrue(isMatching);
    }

    @Test
    public void passwordNotMatchesHashTest() {
        String clearPassword = "Password123!";
        String incorrectHash = "49c6192a47294be98ddc177c9a185a169342168c4308bf0cfc35ef5075d3b9a3";

        boolean isMatching = passwordHashGenerator.matches(clearPassword, incorrectHash);

        assertFalse(isMatching);
    }
}
