package com.nagarro.recruitmenthelper.usermanagement.validationstrategy.impl;

import com.nagarro.recruitmenthelper.usermanagement.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExistingEmailFieldStrategyValidatorTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private ExistingEmailFieldStrategyValidator existingEmailFieldStrategyValidator;

    @Test
    public void testCheckIfValueExists() {
        String value = "test@test.com";
        when(userRepository.findUserByEmail(value)).thenReturn(Optional.empty());

        boolean result = existingEmailFieldStrategyValidator.checkIfValueExists(value, "email");

        assertFalse(result);
        verify(userRepository).findUserByEmail(value);
    }

    @Test
    public void testCheckIfValueExists_fieldIsNotEmail() {
        assertThatThrownBy(() -> existingEmailFieldStrategyValidator.checkIfValueExists(new Object(), "otherField"))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("Field name not supported");
    }

    @Test
    public void testCheckIfValueExists_emailValueIsNull() {
        boolean result = existingEmailFieldStrategyValidator.checkIfValueExists(null, "email");

        assertFalse(result);
    }
}
