package com.nagarro.recruitmenthelper.usermanagement.validationstrategy.impl;

import com.nagarro.recruitmenthelper.usermanagement.repository.UserRepository;
import com.nagarro.recruitmenthelper.usermanagement.validationstrategy.FieldValidationStrategy;
import org.springframework.stereotype.Component;

@Component
public class ExistingEmailFieldStrategyValidator implements FieldValidationStrategy {
    private final UserRepository userRepository;

    public ExistingEmailFieldStrategyValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean checkIfValueExists(Object value, String fieldName) throws UnsupportedOperationException {
        if (!fieldName.equals("email")) {
            throw new UnsupportedOperationException("Field name not supported");
        }
        if (value == null) {
            return false;
        }
        return userRepository.findUserByEmail(value.toString()).isPresent();
    }
}
