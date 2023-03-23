package com.nagarro.candidatemanagement.repository;

import com.nagarro.candidatemanagement.model.Feedback;

import java.util.List;

public interface FeedbackRepository {
    /**
     * Add feedback into the database
     *
     * @param feedback - the feedback to-be-inserted
     */
    void addFeedback(Feedback feedback);

    /**
     * Get feedback with the given candidateId
     *
     * @param candidateId - the id of the candidate
     * @return all feedback with the given candidateId
     */
    List<Feedback> findByCandidateId(long candidateId);
}
