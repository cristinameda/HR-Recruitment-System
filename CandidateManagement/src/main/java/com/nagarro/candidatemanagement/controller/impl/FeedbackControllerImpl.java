package com.nagarro.candidatemanagement.controller.impl;

import com.nagarro.candidatemanagement.controller.FeedbackController;
import com.nagarro.candidatemanagement.controller.dto.FeedbackDTO;
import com.nagarro.candidatemanagement.service.FeedbackService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FeedbackControllerImpl implements FeedbackController {
    private final FeedbackService feedbackService;

    public FeedbackControllerImpl(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @Override
    public ResponseEntity<FeedbackDTO> giveFeedbackToCandidate(long candidateId, FeedbackDTO feedbackDTO) {
        return new ResponseEntity<>(feedbackService.giveFeedbackToCandidate(candidateId, feedbackDTO), HttpStatus.CREATED);
    }
}
