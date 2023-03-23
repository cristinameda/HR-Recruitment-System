package com.nagarro.candidatemanagement.validation.impl;

import com.nagarro.candidatemanagement.exception.CandidateNotFoundException;
import com.nagarro.candidatemanagement.model.Candidate;
import com.nagarro.candidatemanagement.repository.CandidateRepository;
import com.nagarro.candidatemanagement.validation.CandidateValidator;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class InterviewCandidateValidator implements CandidateValidator {
    private final CandidateRepository candidateRepository;

    public InterviewCandidateValidator(CandidateRepository candidateRepository) {
        this.candidateRepository = candidateRepository;
    }

    @Override
    public void validateCandidate(long candidateId) {
        Optional<Candidate> candidate = candidateRepository.findById(candidateId);
        if (candidate.isEmpty() || !hasAssignedUsers(candidate.get())) {
            throw new CandidateNotFoundException("Invalid candidate with id: '" + candidateId + "' selected for interview!");
        }
    }

    private boolean hasAssignedUsers(Candidate candidate) {
        return (candidate.getAssignedUsers().size() == 4) ||
                (candidate.getAssignedUsers().size() == 5);
    }
}
