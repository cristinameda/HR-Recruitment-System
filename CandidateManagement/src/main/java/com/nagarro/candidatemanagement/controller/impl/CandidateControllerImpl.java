package com.nagarro.candidatemanagement.controller.impl;

import com.nagarro.candidatemanagement.controller.CandidateController;
import com.nagarro.candidatemanagement.controller.dto.CandidateDTO;
import com.nagarro.candidatemanagement.controller.dto.UpdateCandidateStatusDTO;
import com.nagarro.candidatemanagement.controller.dto.UserEmailRoleDTO;
import com.nagarro.candidatemanagement.service.CandidateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class CandidateControllerImpl implements CandidateController {
    private final CandidateService candidateService;

    public CandidateControllerImpl(CandidateService candidateService) {
        this.candidateService = candidateService;
    }

    @Override
    public ResponseEntity<CandidateDTO> save(CandidateDTO candidate, MultipartFile cv, MultipartFile gdpr) {
        return new ResponseEntity<>(candidateService.save(candidate, cv, gdpr), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<List<CandidateDTO>> findAll(int pageNo, int pageSize, String field, String filterValue) {
        return new ResponseEntity<>(candidateService.findAll(pageNo, pageSize, field, filterValue), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<CandidateDTO> findById(long candidateId) {
        return new ResponseEntity<>(candidateService.findById(candidateId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> assignUsersToCandidate(long id, List<UserEmailRoleDTO> assignedUsersToCandidateDTO) {
        candidateService.assignUsersToCandidate(id, assignedUsersToCandidateDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<Void> updateCandidateStatus(UpdateCandidateStatusDTO updateCandidateStatusDTO) {
        candidateService.updateCandidateStatus(updateCandidateStatusDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<Void> delete(long id) {
        candidateService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
