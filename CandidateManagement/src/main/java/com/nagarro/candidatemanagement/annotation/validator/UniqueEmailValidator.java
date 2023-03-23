package com.nagarro.candidatemanagement.annotation.validator;

import com.nagarro.candidatemanagement.annotation.UniqueEmail;
import com.nagarro.candidatemanagement.exception.CandidateNotFoundException;
import com.nagarro.candidatemanagement.service.CandidateService;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {
    private final CandidateService candidateService;

    public UniqueEmailValidator(CandidateService candidateService) {
        this.candidateService = candidateService;
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        try {
            candidateService.findByEmail(email);
            return false;
        } catch (CandidateNotFoundException e) {
            return true;
        }
    }
}
