package com.nagarro.candidatemanagement.validation.impl;

import com.nagarro.candidatemanagement.exception.CandidateNotFoundException;
import com.nagarro.candidatemanagement.exception.DuplicateFeedbackException;
import com.nagarro.candidatemanagement.exception.UserNotAssignedException;
import com.nagarro.candidatemanagement.model.Candidate;
import com.nagarro.candidatemanagement.model.Feedback;
import com.nagarro.candidatemanagement.repository.CandidateRepository;
import com.nagarro.candidatemanagement.repository.FeedbackRepository;
import com.nagarro.candidatemanagement.tokenutils.model.UserDetails;
import com.nagarro.candidatemanagement.utils.TestDataBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CandidateFeedbackValidatorImplTest {
    @Mock
    private CandidateRepository candidateRepository;
    @Mock
    private FeedbackRepository feedbackRepository;
    @InjectMocks
    private CandidateFeedbackValidatorImpl candidateFeedbackValidator;

    @Test
    void testValidateCandidateFeedback_valid() {
        Candidate candidate = TestDataBuilder.buildCandidate("candidate", "candidate@email.com");
        candidate.setId(1L);
        UserDetails userDetails = TestDataBuilder.buildUserDetails("user@email.com", "role");
        candidate.setAssignedUsers(List.of("user@email.com"));
        when(candidateRepository.findById(candidate.getId())).thenReturn(Optional.of(candidate));
        when(feedbackRepository.findByCandidateId(candidate.getId())).thenReturn(new ArrayList<>());

        candidateFeedbackValidator.validateCandidateFeedback(candidate.getId(), userDetails);

        verify(candidateRepository).findById(1L);
    }

    @Test
    void testValidateCandidateFeedback_whenCandidateNotFound_shouldThrowCandidateNotFoundException() {
        UserDetails userDetails = TestDataBuilder.buildUserDetails("user@email.com", "role");
        when(candidateRepository.findById(1L)).thenReturn(Optional.empty());

        Exception candidateNotFoundException = assertThrows(CandidateNotFoundException.class, () ->
                candidateFeedbackValidator.validateCandidateFeedback(1L, userDetails));
        assertEquals("Candidate with id: 1 wasn't found!", candidateNotFoundException.getMessage());
    }

    @Test
    void testValidateCandidateFeedback_whenUserNotAssigned_shouldThrowUserNotAssignedException() {
        Candidate candidate = TestDataBuilder.buildCandidate("candidate", "candidate@email.com");
        candidate.setId(1L);
        UserDetails userDetails = TestDataBuilder.buildUserDetails("user@email.com", "role");
        when(candidateRepository.findById(candidate.getId())).thenReturn(Optional.of(candidate));

        Exception userNotAssignedException = assertThrows(UserNotAssignedException.class, () ->
                candidateFeedbackValidator.validateCandidateFeedback(1L, userDetails));
        assertEquals("You are not assigned to candidate 'candidate@email.com'!", userNotAssignedException.getMessage());
    }

    @Test
    void testValidateCandidateFeedback_whenFeedbackAlreadyGiven_shouldThrowDuplicateFeedbackException() {
        Candidate candidate = TestDataBuilder.buildCandidate("candidate", "candidate@email.com");
        candidate.setId(1L);
        UserDetails userDetails = TestDataBuilder.buildUserDetails("user@email.com", "role");
        Feedback feedback = new Feedback("comment", candidate.getId(), userDetails.getEmail(), true, LocalDateTime.now());
        candidate.setAssignedUsers(List.of(userDetails.getEmail()));
        when(candidateRepository.findById(candidate.getId())).thenReturn(Optional.of(candidate));
        when(feedbackRepository.findByCandidateId(candidate.getId())).thenReturn(List.of(feedback));

        Exception duplicateFeedbackException = assertThrows(DuplicateFeedbackException.class, () ->
                candidateFeedbackValidator.validateCandidateFeedback(candidate.getId(), userDetails));
        assertEquals("You already gave feedback to candidate 'candidate@email.com'!", duplicateFeedbackException.getMessage());
    }
}