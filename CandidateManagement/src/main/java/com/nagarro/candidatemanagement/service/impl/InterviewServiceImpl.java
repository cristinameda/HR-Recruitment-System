package com.nagarro.candidatemanagement.service.impl;

import com.nagarro.candidatemanagement.controller.dto.InterviewDTO;
import com.nagarro.candidatemanagement.model.Interview;
import com.nagarro.candidatemanagement.repository.InterviewRepository;
import com.nagarro.candidatemanagement.service.InterviewService;
import com.nagarro.candidatemanagement.validation.CandidateValidator;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class InterviewServiceImpl implements InterviewService {

    private final InterviewRepository interviewRepository;
    private final ModelMapper modelMapper;
    private final CandidateValidator candidateValidator;

    public InterviewServiceImpl(InterviewRepository interviewRepository, ModelMapper modelMapper, CandidateValidator candidateValidator) {
        this.interviewRepository = interviewRepository;
        this.modelMapper = modelMapper;
        this.candidateValidator = candidateValidator;
    }

    @Override
    public InterviewDTO scheduleInterview(InterviewDTO interviewDTO) {
        candidateValidator.validateCandidate(interviewDTO.getCandidateId());
        Interview interview = mapToInterview(interviewDTO);
        return mapToInterviewDTO(interviewRepository.addInterview(interview));
    }

    private Interview mapToInterview(InterviewDTO interviewDTO) {
        return modelMapper.map(interviewDTO, Interview.class);
    }

    private InterviewDTO mapToInterviewDTO(Interview interview) {
        return modelMapper.map(interview, InterviewDTO.class);
    }
}
