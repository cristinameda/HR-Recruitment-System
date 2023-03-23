package com.nagarro.candidatemanagement.validation;

import com.nagarro.candidatemanagement.tokenutils.model.UserDetails;

public interface CandidateFeedbackValidator {
    void validateCandidateFeedback(long id, UserDetails userDetails);
}
