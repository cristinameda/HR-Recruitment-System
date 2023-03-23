package com.nagarro.recruitmenthelper.usermanagement.service.security;

public interface PasswordHashGenerator {
    /**
     * Generates a hash for encrypting a password.
     *
     * @param clearPassword - the password to be encrypted for security reasons
     * @return the hash - a string consisting in a sequence of bits converted to hexadecimal
     */
    String hashPassword(String clearPassword);

    /**
     * Checks if a password has the appropriate hash.
     *
     * @param clearPassword - the password to be encrypted for security reasons
     * @param hash          - the expected hash
     * @return boolean value - whether the password and hash match after hash calculation
     */
    boolean matches(String clearPassword, String hash);
}
