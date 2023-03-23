package com.nagarro.candidatemanagement.controller.impl;

import com.nagarro.candidatemanagement.controller.InterviewController;
import com.nagarro.candidatemanagement.controller.dto.InterviewDTO;
import com.nagarro.candidatemanagement.service.InterviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InterviewControllerImpl implements InterviewController {
    private final InterviewService interviewService;

    public InterviewControllerImpl(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    @Override
    public ResponseEntity<InterviewDTO> scheduleInterview(InterviewDTO interviewDTO) {
        return new ResponseEntity<>(interviewService.scheduleInterview(interviewDTO), HttpStatus.CREATED);
    }
}
