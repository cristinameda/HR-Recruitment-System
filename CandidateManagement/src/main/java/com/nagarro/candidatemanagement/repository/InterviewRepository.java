package com.nagarro.candidatemanagement.repository;

import com.nagarro.candidatemanagement.model.Interview;

public interface InterviewRepository {
    /**
     * Adds a new interview in the database.
     *
     * @param interview the interview to be scheduled
     * @return the interview just added with the id set
     */
    Interview addInterview(Interview interview);
}
