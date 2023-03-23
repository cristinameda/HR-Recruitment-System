package com.nagarro.candidatemanagement.repository;

import com.nagarro.candidatemanagement.model.Candidate;

import java.util.List;
import java.util.Optional;

public interface CandidateRepository {
    /**
     * Stores a new candidate in the database.
     *
     * @param candidate - the candidate to be inserted
     * @return the stored candidate
     */
    Candidate save(Candidate candidate);

    /**
     * Retrieves all candidates.
     *
     * @param pageNo   page number
     * @param pageSize page size
     * @return - a list with all database candidates
     */
    List<Candidate> findAll(int pageNo, int pageSize);

    /**
     * Retrieves candidates that match the filterValue with their given field.
     *
     * @param pageNo      - page number
     * @param pageSize    - page size
     * @param field       - the field which will be filtered on
     * @param filterValue - the value which will be matched
     * @return - a list with the filtered candidates
     * @throws com.nagarro.candidatemanagement.repository.exception.RepositoryException if the field is invalid
     */
    List<Candidate> findAllByField(int pageNo, int pageSize, String field, String filterValue);

    /**
     * Retrieves candidates that have the given assigned user.
     *
     * @param pageNo    - page number
     * @param pageSize  - page size
     * @param userEmail - the user email
     * @return - a list with the candidates that have the given user assigned to
     */
    List<Candidate> findAllByAssignedUser(Integer pageNo, Integer pageSize, String userEmail);

    /**
     * Retrieves a candidate by ID.
     *
     * @param candidateId id of requested candidate
     * @return requested candidate
     */
    Optional<Candidate> findById(Long candidateId);

    /**
     * Retrieves a candidate by email.
     *
     * @param email email of requested candidate
     * @return requested candidate
     */
    Optional<Candidate> findByEmail(String email);

    /**
     * Deletes from the database a candidate by ID.
     *
     * @param id the candidate's id
     * @return true if the candidate is successfully deleted, false otherwise
     */
    boolean delete(long id);

    /**
     * Updates the assigned users for a candidate.
     *
     * @param candidate the candidate whose assigned users will be updated
     */
    void updateCandidateAssignedUsers(Candidate candidate);

    /**
     * Updates the assigned status for a candidate.
     *
     * @param candidate - the candidate whose assigned status will be updated
     */
    void updateCandidateStatus(Candidate candidate);
}
