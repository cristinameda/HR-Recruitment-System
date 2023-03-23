package com.nagarro.recruitmenthelper.usermanagement.service.validation;

import com.nagarro.recruitmenthelper.usermanagement.validationstrategy.FieldValidationStrategy;
import com.nagarro.recruitmenthelper.usermanagement.validationstrategy.validation.UniqueValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.validation.ConstraintValidatorContext;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class UniqueValidatorTest {
    private UniqueValidator underTestUniqueValidator;
    @Mock
    private ApplicationContext context;
    @Mock
    private FieldValidationStrategy fieldValidationStrategy;
    @Mock
    private ConstraintValidatorContext constraintValidatorContext;

    @BeforeEach
    public void setUp() {
        underTestUniqueValidator = spy(new UniqueValidator(context));
    }

    @Test
    void testUniqueValidator() {
        doReturn(fieldValidationStrategy).when(underTestUniqueValidator).getFieldValidationStrategy();
        String expectedFieldName = "email";
        doReturn(expectedFieldName).when(underTestUniqueValidator).getFieldName();
        Object object = new Object();
        when(fieldValidationStrategy.checkIfValueExists(object, expectedFieldName)).thenReturn(false);

        boolean result = underTestUniqueValidator.isValid(object, constraintValidatorContext);

        assertTrue(result);
        verify(fieldValidationStrategy).checkIfValueExists(object, expectedFieldName);
    }
}
