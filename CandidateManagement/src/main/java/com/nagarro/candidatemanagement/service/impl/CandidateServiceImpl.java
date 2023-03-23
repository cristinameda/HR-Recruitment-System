package com.nagarro.candidatemanagement.service.impl;

import com.nagarro.candidatemanagement.config.BearerTokenWrapper;
import com.nagarro.candidatemanagement.controller.dto.CandidateDTO;
import com.nagarro.candidatemanagement.controller.dto.FileDTO;
import com.nagarro.candidatemanagement.controller.dto.UpdateCandidateStatusDTO;
import com.nagarro.candidatemanagement.controller.dto.UserEmailRoleDTO;
import com.nagarro.candidatemanagement.exception.AssignUsersException;
import com.nagarro.candidatemanagement.exception.CandidateNotFoundException;
import com.nagarro.candidatemanagement.exception.CorruptedFileException;
import com.nagarro.candidatemanagement.gateway.UsersValidator;
import com.nagarro.candidatemanagement.model.Candidate;
import com.nagarro.candidatemanagement.model.FileType;
import com.nagarro.candidatemanagement.repository.CandidateRepository;
import com.nagarro.candidatemanagement.repository.FeedbackRepository;
import com.nagarro.candidatemanagement.repository.exception.RepositoryException;
import com.nagarro.candidatemanagement.service.CandidateService;
import com.nagarro.candidatemanagement.tokenutils.TokenManager;
import com.nagarro.candidatemanagement.tokenutils.model.UserDetails;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class CandidateServiceImpl implements CandidateService {
    private static final Logger logger = LoggerFactory.getLogger(CandidateServiceImpl.class);
    private static final String HR_REPRESENTATIVE = "HrRepresentative";
    private static final String TECHNICAL_INTERVIEWER = "TechnicalInterviewer";
    private static final String PTE = "PTE";
    private final CandidateRepository candidateRepository;
    private final FeedbackRepository feedbackRepository;
    private final ModelMapper modelMapper;
    private final UsersValidator usersValidator;
    private final TokenManager tokenManager;
    private final BearerTokenWrapper bearerTokenWrapper;

    public CandidateServiceImpl(CandidateRepository candidateRepository, FeedbackRepository feedbackRepository, ModelMapper modelMapper,
                                UsersValidator usersValidator, TokenManager tokenManager,
                                BearerTokenWrapper bearerTokenWrapper) {
        this.candidateRepository = candidateRepository;
        this.feedbackRepository = feedbackRepository;
        this.modelMapper = modelMapper;
        this.usersValidator = usersValidator;
        this.tokenManager = tokenManager;
        this.bearerTokenWrapper = bearerTokenWrapper;
    }

    @Transactional
    @Override
    public CandidateDTO save(CandidateDTO candidateDTO, MultipartFile cv, MultipartFile gdpr) {
        CandidateDTO candidateDTOWithFilesSet = setFiles(candidateDTO, cv, gdpr);
        Candidate candidate = mapToCandidate(candidateDTOWithFilesSet);
        Candidate newCandidate = candidateRepository.save(candidate);
        logger.info("Candidate {} saved", newCandidate.getEmail());
        return mapToCandidateDTO(newCandidate);
    }

    @Override
    public List<CandidateDTO> findAll(Integer pageNo, Integer pageSize, String field, String filterValue) {
        UserDetails userDetails = tokenManager.getUserDetailsFromToken(bearerTokenWrapper.getToken());
        String role = userDetails.getRole();
        if (role.equals(TECHNICAL_INTERVIEWER)) {
            return findAllByAssignedUser(pageNo, pageSize, userDetails.getEmail());
        }
        if (role.equals(PTE) || role.equals(HR_REPRESENTATIVE)) {
            if (field == null || field.trim().isEmpty()) {
                return findAll(pageNo, pageSize);
            }
            return findAllFilterByField(pageNo, pageSize, field, filterValue);
        }
        throw new IllegalStateException("Role '" + role + "' is unauthorized!");
    }

    @Override
    public CandidateDTO findById(Long candidateId) {
        return candidateRepository.findById(candidateId)
                .map(this::mapToCandidateDTO)
                .orElseThrow(() -> new CandidateNotFoundException("Candidate with id: " + candidateId + " wasn't found!"));
    }

    @Override
    public CandidateDTO findByEmail(String email) {
        return candidateRepository.findByEmail(email)
                .map(this::mapToCandidateDTO)
                .orElseThrow(() -> new CandidateNotFoundException("Candidate with email: " + email + " wasn't found!"));
    }

    @Override
    public void delete(long id) {
        if (!candidateRepository.delete(id)) {
            throw new CandidateNotFoundException("Candidate with id " + id + " does not exist!");
        }
    }

    @Override
    public void assignUsersToCandidate(long id, List<UserEmailRoleDTO> assignedUsersToCandidateDTO) {
        CandidateDTO candidateDTO = findById(id);
        if (!usersValidator.areRequestedRolesValid(assignedUsersToCandidateDTO)) {
            throw new AssignUsersException("Invalid number of requested roles!");
        }
        if (!usersValidator.areUsersValid(assignedUsersToCandidateDTO)) {
            throw new AssignUsersException("Users are not valid!");
        }
        List<String> userEmails = assignedUsersToCandidateDTO.stream()
                .map(UserEmailRoleDTO::getEmail)
                .toList();
        candidateDTO.setAssignedUsers(userEmails);
        candidateRepository.updateCandidateAssignedUsers(mapToCandidate(candidateDTO));
    }

    @Override
    public void updateCandidateStatus(UpdateCandidateStatusDTO updateCandidateStatusDTO) {
        CandidateDTO candidateDTO = findByEmail(updateCandidateStatusDTO.getEmail());
        candidateDTO.setStatus(updateCandidateStatusDTO.getStatus());
        candidateRepository.updateCandidateStatus(mapToCandidate(candidateDTO));
    }

    private CandidateDTO mapToCandidateDTO(Candidate candidate) {
        attachFeedback(candidate);
        return modelMapper.map(candidate, CandidateDTO.class);
    }

    private void attachFeedback(Candidate candidate) {
        candidate.setFeedback(feedbackRepository.findByCandidateId(candidate.getId()));
    }

    private Candidate mapToCandidate(CandidateDTO candidateDTO) {
        return modelMapper.map(candidateDTO, Candidate.class);
    }

    private List<CandidateDTO> findAll(Integer pageNo, Integer pageSize) {
        return mapListToDTO(candidateRepository.findAll(pageNo, pageSize));
    }

    private List<CandidateDTO> findAllFilterByField(Integer pageNo, Integer pageSize, String field, String filterValue) {
        if (filterValue == null) {
            throw new IllegalArgumentException("Filter value cannot be null when filtering!");
        }
        try {
            return mapListToDTO(candidateRepository.findAllByField(pageNo, pageSize, field, filterValue));
        } catch (RepositoryException exception) {
            throw new IllegalArgumentException(exception.getMessage(), exception);
        }
    }

    private List<CandidateDTO> findAllByAssignedUser(Integer pageNo, Integer pageSize, String userEmail) {
        return mapListToDTO(candidateRepository.findAllByAssignedUser(pageNo, pageSize, userEmail));
    }

    private List<CandidateDTO> mapListToDTO(List<Candidate> candidates) {
        return candidates.stream()
                .map(this::mapToCandidateDTO)
                .toList();
    }

    private CandidateDTO setFiles(CandidateDTO candidateDTO, MultipartFile cv, MultipartFile gdpr) {
        String cvFileName = StringUtils.cleanPath(cv.getOriginalFilename());
        String gdprFileName = StringUtils.cleanPath(gdpr.getOriginalFilename());
        try {
            FileDTO cvFile = new FileDTO(cvFileName, FileType.CV, cv.getBytes());
            FileDTO gdprFile = new FileDTO(gdprFileName, FileType.GDPR, gdpr.getBytes());
            candidateDTO.setFiles(List.of(cvFile, gdprFile));
        } catch (IOException e) {
            throw new CorruptedFileException("Files are invalid: " + e);
        }
        return candidateDTO;
    }
}
