package com.nagarro.candidatemanagement.validation;

import com.nagarro.candidatemanagement.annotation.validator.FileValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import javax.validation.ConstraintValidatorContext;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class NotEmptyFileValidatorTest {
    @InjectMocks
    private FileValidator underTestFiledValidator;
    @Mock
    private ConstraintValidatorContext constraintValidatorContext;

    @Test
    void testUniqueValidator_shouldReturnTrue() {
        MockMultipartFile firstFile = new MockMultipartFile("CV", "filename.txt", "text/plain", "some xml".getBytes());
        boolean result = underTestFiledValidator.isValid(firstFile, constraintValidatorContext);

        assertTrue(result);
    }

    @Test
    void testUniqueValidator_shouldReturnFalse() {
        MockMultipartFile firstFile = new MockMultipartFile("CV", "filename.txt", "text/plain", "".getBytes());
        boolean result = underTestFiledValidator.isValid(firstFile, constraintValidatorContext);

        assertFalse(result);
    }
}
