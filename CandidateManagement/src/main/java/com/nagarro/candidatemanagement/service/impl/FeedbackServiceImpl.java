package com.nagarro.candidatemanagement.service.impl;

import com.nagarro.candidatemanagement.config.BearerTokenWrapper;
import com.nagarro.candidatemanagement.controller.dto.FeedbackDTO;
import com.nagarro.candidatemanagement.model.Feedback;
import com.nagarro.candidatemanagement.repository.FeedbackRepository;
import com.nagarro.candidatemanagement.service.FeedbackService;
import com.nagarro.candidatemanagement.tokenutils.TokenManager;
import com.nagarro.candidatemanagement.tokenutils.model.UserDetails;
import com.nagarro.candidatemanagement.validation.CandidateFeedbackValidator;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class FeedbackServiceImpl implements FeedbackService {
    private final TokenManager tokenManager;
    private final BearerTokenWrapper bearerTokenWrapper;
    private final FeedbackRepository feedbackRepository;
    private final ModelMapper mapper;
    private final CandidateFeedbackValidator candidateFeedbackValidator;

    public FeedbackServiceImpl(TokenManager tokenManager, BearerTokenWrapper bearerTokenWrapper, FeedbackRepository feedbackRepository, ModelMapper mapper, CandidateFeedbackValidator candidateFeedbackValidator) {
        this.tokenManager = tokenManager;
        this.bearerTokenWrapper = bearerTokenWrapper;
        this.feedbackRepository = feedbackRepository;
        this.mapper = mapper;
        this.candidateFeedbackValidator = candidateFeedbackValidator;
    }

    @Override
    public FeedbackDTO giveFeedbackToCandidate(long id, FeedbackDTO feedbackDTO) {
        UserDetails userDetails = tokenManager.getUserDetailsFromToken(bearerTokenWrapper.getToken());
        candidateFeedbackValidator.validateCandidateFeedback(id, userDetails);
        feedbackDTO.setCandidateId(id);
        feedbackDTO.setUserEmail(userDetails.getEmail());
        feedbackDTO.setDate(LocalDateTime.now().withNano(0));
        feedbackRepository.addFeedback(mapToFeedback(feedbackDTO));
        return feedbackDTO;
    }

    private Feedback mapToFeedback(FeedbackDTO feedbackDTO) {
        return mapper.map(feedbackDTO, Feedback.class);
    }
}
