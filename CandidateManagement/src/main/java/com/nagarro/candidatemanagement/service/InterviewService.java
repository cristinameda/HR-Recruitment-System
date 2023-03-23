package com.nagarro.candidatemanagement.service;

import com.nagarro.candidatemanagement.controller.dto.InterviewDTO;

public interface InterviewService {

    /**
     * Schedules a new interview.
     *
     * @param interviewDTO the interview
     * @return the interview
     */
    InterviewDTO scheduleInterview(InterviewDTO interviewDTO);
}
