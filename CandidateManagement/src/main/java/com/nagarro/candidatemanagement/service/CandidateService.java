package com.nagarro.candidatemanagement.service;

import com.nagarro.candidatemanagement.controller.dto.CandidateDTO;
import com.nagarro.candidatemanagement.controller.dto.UpdateCandidateStatusDTO;
import com.nagarro.candidatemanagement.controller.dto.UserEmailRoleDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CandidateService {
    /**
     * Stores a new candidate in the database.
     *
     * @param candidate the candidate to be inserted
     * @return the stored candidate
     */
    CandidateDTO save(CandidateDTO candidate, MultipartFile cv, MultipartFile gdpr);

    /**
     * Retrieve candidates. Filter them by field.
     *
     * @param pageNo      page number
     * @param pageSize    page size
     * @param field       the field which will be filtered
     * @param filterValue the filtered value
     * @return a list with the candidates
     */
    List<CandidateDTO> findAll(Integer pageNo, Integer pageSize, String field, String filterValue);

    /**
     * Retrieve a candidate
     *
     * @param candidateId id of requested candidate
     * @return requested candidate
     */
    CandidateDTO findById(Long candidateId);

    /**
     * Retrieves a candidate by email.
     *
     * @param email email of requested candidate
     * @return requested candidate
     */
    CandidateDTO findByEmail(String email);

    /**
     * Deletes from the database a candidate by ID.
     *
     * @param id the candidate's id
     */
    void delete(long id);

    /**
     * Assigns users to a candidate.
     *
     * @param id                          - the candidate's id
     * @param assignedUsersToCandidateDTO - the users to be assigned to the candidate
     */
    void assignUsersToCandidate(long id, List<UserEmailRoleDTO> assignedUsersToCandidateDTO);

    /**
     * Assigns status to a candidate.
     *
     * @param updateCandidateStatusDTO - the candidate's email and status
     */
    void updateCandidateStatus(UpdateCandidateStatusDTO updateCandidateStatusDTO);


}
