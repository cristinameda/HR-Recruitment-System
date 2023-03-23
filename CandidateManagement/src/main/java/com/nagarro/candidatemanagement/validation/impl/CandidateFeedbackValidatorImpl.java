package com.nagarro.candidatemanagement.validation.impl;

import com.nagarro.candidatemanagement.exception.CandidateNotFoundException;
import com.nagarro.candidatemanagement.exception.DuplicateFeedbackException;
import com.nagarro.candidatemanagement.exception.UserNotAssignedException;
import com.nagarro.candidatemanagement.model.Candidate;
import com.nagarro.candidatemanagement.model.Feedback;
import com.nagarro.candidatemanagement.repository.CandidateRepository;
import com.nagarro.candidatemanagement.repository.FeedbackRepository;
import com.nagarro.candidatemanagement.tokenutils.model.UserDetails;
import com.nagarro.candidatemanagement.validation.CandidateFeedbackValidator;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CandidateFeedbackValidatorImpl implements CandidateFeedbackValidator {
    private final CandidateRepository candidateRepository;
    private final FeedbackRepository feedbackRepository;

    public CandidateFeedbackValidatorImpl(CandidateRepository candidateRepository, FeedbackRepository feedbackRepository) {
        this.candidateRepository = candidateRepository;
        this.feedbackRepository = feedbackRepository;
    }

    @Override
    public void validateCandidateFeedback(long id, UserDetails userDetails) {
        Candidate candidate = candidateRepository.findById(id).orElseThrow(() ->
                new CandidateNotFoundException("Candidate with id: " + id + " wasn't found!"));
        if (!candidate.getAssignedUsers().contains(userDetails.getEmail())) {
            throw new UserNotAssignedException("You are not assigned to candidate '" + candidate.getEmail() + "'!");
        }
        List<Feedback> feedbackList = feedbackRepository.findByCandidateId(id);
        feedbackList.stream()
                .filter(existingFeedback ->
                        existingFeedback.getUserEmail().equals(userDetails.getEmail())
                )
                .findAny()
                .ifPresent((optional) -> {
                    throw new DuplicateFeedbackException("You already gave feedback to candidate '" + candidate.getEmail() + "'!");
                });
    }
}
