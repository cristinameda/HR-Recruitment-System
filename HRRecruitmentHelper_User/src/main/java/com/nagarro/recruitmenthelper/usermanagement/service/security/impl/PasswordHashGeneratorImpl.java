package com.nagarro.recruitmenthelper.usermanagement.service.security.impl;

import com.nagarro.recruitmenthelper.usermanagement.service.security.PasswordHashGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class PasswordHashGeneratorImpl implements PasswordHashGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordHashGeneratorImpl.class);
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final int SALT = 132831;

    @Override
    public String hashPassword(String clearPassword) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(HASH_ALGORITHM);
            messageDigest.reset();
            messageDigest.update(getSaltBytes());
            byte[] hash = messageDigest.digest(clearPassword.getBytes());
            return getBytesToStringHex(hash);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("PasswordHashGeneratorImpl: generateHash. Could not generate hash for user password!", e);
            return null;
        }
    }

    @Override
    public boolean matches(String clearPassword, String hash) {
        return hashPassword(clearPassword).equals(hash);
    }

    private String getBytesToStringHex(byte[] bytes) {
        StringBuilder hex = new StringBuilder();

        for (byte aByte : bytes) {
            hex.append(Integer.toString((aByte & 0xff) + 0x100, 16)
                    .substring(1));
        }

        return hex.toString();
    }

    private byte[] getSaltBytes() {
        BigInteger bigInt = BigInteger.valueOf(SALT);
        return bigInt.toByteArray();
    }
}
