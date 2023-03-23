package com.nagarro.candidatemanagement.service;

import com.nagarro.candidatemanagement.controller.dto.FeedbackDTO;

public interface FeedbackService {
    /**
     * Assigns feedback to a candidate.
     *
     * @param id          - the candidate's id
     * @param feedbackDTO - the feedback
     */
    FeedbackDTO giveFeedbackToCandidate(long id, FeedbackDTO feedbackDTO);
}
