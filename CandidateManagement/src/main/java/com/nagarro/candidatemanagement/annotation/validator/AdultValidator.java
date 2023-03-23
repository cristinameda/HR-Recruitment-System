package com.nagarro.candidatemanagement.annotation.validator;

import com.nagarro.candidatemanagement.annotation.Adult;
import java.time.LocalDate;
import java.util.Calendar;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AdultValidator implements ConstraintValidator<Adult, LocalDate> {
    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext constraintValidatorContext) {
        if (date == null) {
            return false;
        }
        int yearOfBirth = date.getYear();
        return (Calendar.getInstance().get(Calendar.YEAR) - yearOfBirth) >= 18;
    }
}
