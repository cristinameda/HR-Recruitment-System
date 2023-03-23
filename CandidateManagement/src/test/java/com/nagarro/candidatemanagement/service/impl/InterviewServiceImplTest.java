package com.nagarro.candidatemanagement.service.impl;

import com.nagarro.candidatemanagement.controller.dto.InterviewDTO;
import com.nagarro.candidatemanagement.exception.CandidateNotFoundException;
import com.nagarro.candidatemanagement.model.Interview;
import com.nagarro.candidatemanagement.repository.InterviewRepository;
import com.nagarro.candidatemanagement.utils.TestDataBuilder;
import com.nagarro.candidatemanagement.validation.CandidateValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InterviewServiceImplTest {

    @Mock
    private ModelMapper modelMapper;
    @Mock
    private CandidateValidator candidateValidator;
    @Mock
    private InterviewRepository interviewRepository;
    @InjectMocks
    private InterviewServiceImpl interviewServiceImpl;
    @Captor
    private ArgumentCaptor<Interview> interviewArgumentCaptor;

    @Test
    public void testScheduleInterview() {
        Interview interview = TestDataBuilder.buildInterview(1L);
        InterviewDTO interviewDTO = TestDataBuilder.buildInterviewDTO(1L);

        doNothing().when(candidateValidator).validateCandidate(1L);
        when(modelMapper.map(interview, InterviewDTO.class)).thenReturn(interviewDTO);
        when(modelMapper.map(interviewDTO, Interview.class)).thenReturn(interview);
        when(interviewRepository.addInterview(interview)).thenReturn(interview);

        InterviewDTO returnedInterviewDTO = interviewServiceImpl.scheduleInterview(interviewDTO);

        verify(candidateValidator).validateCandidate(1L);
        verify(interviewRepository).addInterview(interviewArgumentCaptor.capture());
        assertNotNull(returnedInterviewDTO);
        assertEquals(interview, interviewArgumentCaptor.getValue());
    }

    @Test
    public void testScheduleInterview_whenNoCandidateFound_shouldThrowCandidateNotFoundException() {
        InterviewDTO interviewDTO = TestDataBuilder.buildInterviewDTO(1L);

        doThrow(new CandidateNotFoundException("Invalid candidate with id: '1' selected for interview!"))
                .when(candidateValidator).validateCandidate(1L);

        CandidateNotFoundException exception = assertThrows(
                CandidateNotFoundException.class, () -> interviewServiceImpl.scheduleInterview(interviewDTO));
        assertEquals("Invalid candidate with id: '1' selected for interview!", exception.getMessage());
        verify(candidateValidator).validateCandidate(1L);
    }
}
