package com.nagarro.candidatemanagement.service.impl;

import com.nagarro.candidatemanagement.config.BearerTokenWrapper;
import com.nagarro.candidatemanagement.controller.dto.FeedbackDTO;
import com.nagarro.candidatemanagement.model.Feedback;
import com.nagarro.candidatemanagement.repository.FeedbackRepository;
import com.nagarro.candidatemanagement.tokenutils.TokenManager;
import com.nagarro.candidatemanagement.tokenutils.model.UserDetails;
import com.nagarro.candidatemanagement.utils.TestDataBuilder;
import com.nagarro.candidatemanagement.validation.CandidateFeedbackValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FeedbackServiceImplTest {
    @Mock
    private TokenManager tokenManager;
    @Mock
    private BearerTokenWrapper bearerTokenWrapper;
    @Mock
    private FeedbackRepository feedbackRepository;
    @Mock
    private ModelMapper mapper;
    @Mock
    private CandidateFeedbackValidator candidateFeedbackValidator;
    @InjectMocks
    private FeedbackServiceImpl feedbackService;

    @Test
    public void testGiveFeedbackToCandidate() {
        LocalDateTime localDateTime = LocalDateTime.now().withNano(0);
        Feedback feedback = new Feedback("comment", 1, "email", true, localDateTime);
        FeedbackDTO feedbackDTO = new FeedbackDTO("comment", null, "email", true, null);
        FeedbackDTO expectedFeedbackDTO =
                new FeedbackDTO("comment", 1L, "email", true, localDateTime);
        UserDetails userDetails = TestDataBuilder.buildUserDetails("email", "Token");

        when(bearerTokenWrapper.getToken()).thenReturn("Token");
        when(tokenManager.getUserDetailsFromToken("Token")).thenReturn(userDetails);
        doNothing().when(candidateFeedbackValidator).validateCandidateFeedback(1L, userDetails);
        when(mapper.map(feedbackDTO, Feedback.class)).thenReturn(feedback);

        FeedbackDTO actualFeedback = feedbackService.giveFeedbackToCandidate(1L, feedbackDTO);

        verify(feedbackRepository).addFeedback(feedback);
        verify(mapper).map(feedbackDTO, Feedback.class);
        assertEquals(expectedFeedbackDTO.getUserEmail(), actualFeedback.getUserEmail());
        assertEquals(expectedFeedbackDTO.getCandidateId(), actualFeedback.getCandidateId());
        assertEquals(expectedFeedbackDTO.getDate(), actualFeedback.getDate());
    }
}
