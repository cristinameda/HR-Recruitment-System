package com.nagarro.candidatemanagement.repository.impl;

import com.nagarro.candidatemanagement.controller.dto.CandidateDTO;
import com.nagarro.candidatemanagement.controller.dto.InterestedPositionDTO;
import com.nagarro.candidatemanagement.controller.dto.UserEmailRoleDTO;
import com.nagarro.candidatemanagement.model.Candidate;
import com.nagarro.candidatemanagement.model.CandidateStatus;
import com.nagarro.candidatemanagement.model.File;
import com.nagarro.candidatemanagement.model.FileType;
import com.nagarro.candidatemanagement.model.InterestedPosition;
import com.nagarro.candidatemanagement.repository.CandidateRepository;
import com.nagarro.candidatemanagement.repository.exception.RepositoryException;
import com.nagarro.candidatemanagement.utils.TestDataBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
@Sql({"classpath:script/drop_schema.sql", "classpath:script/create_schema.sql", "classpath:script/insertposition_schema.sql",
        "classpath:script/insertcandidate_schema.sql", "classpath:script/insertcandidateuser_schema.sql",
        "classpath:script/insertfile_schema.sql", "classpath:script/insertcandidateposition_schema.sql"})
class ITJdbcCandidateRepository {

    @Autowired
    private CandidateRepository candidateRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testAddCandidate() {
        Candidate candidate = TestDataBuilder.buildCandidate("Candidate1", "candidate1@yahoo.com");
        candidate.setFiles(List.of(new File("test", FileType.CV, "hello".getBytes()), new File("test", FileType.GDPR, "hello".getBytes())));
        int initialRowsNumberCandidates = JdbcTestUtils.countRowsInTable(jdbcTemplate, "candidates");
        int initialRowsNumberFiles = JdbcTestUtils.countRowsInTable(jdbcTemplate, "files");

        Candidate returnedCandidate = candidateRepository.save(candidate);
        int actualRowsNumberCandidates = JdbcTestUtils.countRowsInTable(jdbcTemplate, "candidates");
        int actualRowsNumberFiles = JdbcTestUtils.countRowsInTable(jdbcTemplate, "files");

        assertEquals(candidate.getId(), returnedCandidate.getId());
        assertNotNull(returnedCandidate);
        assertEquals(actualRowsNumberCandidates, initialRowsNumberCandidates + 1);
        assertEquals(actualRowsNumberFiles, initialRowsNumberFiles + 2);
    }

    @Test
    public void testFindAll_shouldReturnAllCandidates() {
        List<Candidate> candidateList = candidateRepository.findAll(1, 6);
        List<CandidateDTO> expectedList = TestDataBuilder.candidateList();

        assertEquals(expectedList.size(), candidateList.size());
        assertEquals(expectedList.get(0).getId(), candidateList.get(0).getId());
        assertEquals(expectedList.get(5).getId(), candidateList.get(5).getId());

        List<InterestedPositionDTO> expectedPositions = expectedList.get(1).getInterestedPositionsDTO();
        List<InterestedPosition> actualPositions = candidateList.get(1).getInterestedPositions();

        assertEquals(expectedPositions.size(), actualPositions.size());
    }

    @Test
    public void testFindAllByField_whenFilterByEmail_shouldReturnCandidatesFilteredByEmail() {
        List<Candidate> candidates = candidateRepository.findAllByField(1, 6, "email", "ate");

        candidates.forEach(candidate -> assertTrue(candidate.getEmail().contains("ate")));
    }

    @Test
    public void testFindAllByField_whenFilterByName_shouldReturnCandidatesFilteredByName() {
        List<Candidate> candidates = candidateRepository.findAllByField(1, 6, "name", "ate");

        candidates.forEach(candidate -> assertTrue(candidate.getEmail().contains("ate")));
    }

    @Test
    public void testFindAllByField_whenInvalidFilter_shouldThrowRepositoryException() {
        RepositoryException exception = assertThrows(RepositoryException.class,
                () -> candidateRepository.findAllByField(
                        1, 6, "invalid_field", "ate"));
        assertEquals(exception.getMessage(), "Cannot filter candidates by field 'invalid_field'!");
    }

    @Test
    public void testFindAllByAssignedUser() {
        List<Candidate> candidates = candidateRepository.findAllByAssignedUser(1, 6, "user1@email.com");

        candidates.forEach(candidate -> assertTrue(candidate.getAssignedUsers().contains("user1@email.com")));
    }

    @Test
    public void testFindById_shouldReturnRequestedCandidate() {
        CandidateDTO expectedCandidate = TestDataBuilder.buildCandidateDTO(2L, "Candidate2", "candidate2@yahoo.com");
        Optional<Candidate> actualCandidate = candidateRepository.findById(2L);

        assertTrue(actualCandidate.isPresent());
        assertEquals(expectedCandidate.getId(), actualCandidate.get().getId());

        List<InterestedPositionDTO> expectedPositions = expectedCandidate.getInterestedPositionsDTO();
        List<InterestedPosition> actualPositions = actualCandidate.get().getInterestedPositions();

        assertEquals(expectedPositions.size(), actualPositions.size());
    }

    @Test
    void testFindByEmail_shouldReturnRequestedCandidate() {
        CandidateDTO expectedCandidate = TestDataBuilder.buildCandidateDTO(2L, "Candidate2", "candidate2@yahoo.com");
        Optional<Candidate> actualCandidate = candidateRepository.findByEmail("candidate2@yahoo.com");

        assertTrue(actualCandidate.isPresent());
        assertEquals(expectedCandidate.getEmail(), actualCandidate.get().getEmail());

        List<InterestedPositionDTO> expectedPositions = expectedCandidate.getInterestedPositionsDTO();
        List<InterestedPosition> actualPositions = actualCandidate.get().getInterestedPositions();

        assertEquals(expectedPositions.size(), actualPositions.size());
    }

    @Test
    public void testFindById_shouldReturnEmptyOptional() {
        Optional<Candidate> optionalCandidate = candidateRepository.findById(10L);

        assertTrue(optionalCandidate.isEmpty());
    }

    @Test
    public void testFindByEmail_shouldReturnEmptyOptional() {
        Optional<Candidate> optionalCandidate = candidateRepository.findByEmail("candidate10@yahoo.com");

        assertTrue(optionalCandidate.isEmpty());
    }


    @Test
    void testDeleteCandidate() {
        int initialRowsNumCandidates = JdbcTestUtils.countRowsInTable(jdbcTemplate, "candidates");
        int initialRowsNumCandidatesPositions = JdbcTestUtils.countRowsInTable(jdbcTemplate, "candidates_positions");
        int initialRowsNumFiles = JdbcTestUtils.countRowsInTable(jdbcTemplate, "files");

        candidateRepository.delete(1L);

        int actualRowsNumCandidates = JdbcTestUtils.countRowsInTable(jdbcTemplate, "candidates");
        int actualRowsNumCandidatesPositions = JdbcTestUtils.countRowsInTable(jdbcTemplate, "candidates_positions");
        int actualRowsNumCandidatesFiles = JdbcTestUtils.countRowsInTable(jdbcTemplate, "files");

        assertEquals(initialRowsNumCandidates - 1, actualRowsNumCandidates);
        assertEquals(initialRowsNumCandidatesPositions - 1, actualRowsNumCandidatesPositions);
        assertEquals(initialRowsNumFiles - 2, actualRowsNumCandidatesFiles);
    }

    @Test
    void testDeleteCandidate_shouldReturnFalse() {
        int initialRowsNumCandidates = JdbcTestUtils.countRowsInTable(jdbcTemplate, "candidates");
        int initialRowsNumCandidatesPositions = JdbcTestUtils.countRowsInTable(jdbcTemplate, "candidates_positions");

        boolean isDeleted = candidateRepository.delete(444L);

        int actualRowsNumCandidates = JdbcTestUtils.countRowsInTable(jdbcTemplate, "candidates");
        int actualRowsNumCandidatesPositions = JdbcTestUtils.countRowsInTable(jdbcTemplate, "candidates_positions");

        assertEquals(initialRowsNumCandidates, actualRowsNumCandidates);
        assertEquals(initialRowsNumCandidatesPositions, actualRowsNumCandidatesPositions);
        assertFalse(isDeleted);
    }

    @Test
    void testUpdateCandidateAssignedUsers() {
        List<UserEmailRoleDTO> users = TestDataBuilder.generateUserEmailRoleDTOListWithRequiredRoles();
        Candidate candidate = TestDataBuilder.buildCandidate("Candidate1", "candidate1@yahoo.com");
        candidate.setId(1L);

        List<String> userEmails = users.stream()
                .map(UserEmailRoleDTO::getEmail)
                .toList();

        candidate.setAssignedUsers(userEmails);

        int initialRowsNumCandidatesUsers = JdbcTestUtils.countRowsInTable(jdbcTemplate, "candidates_users");

        candidateRepository.updateCandidateAssignedUsers(candidate);

        int actualRowsNumCandidatesUsers = JdbcTestUtils.countRowsInTable(jdbcTemplate, "candidates_users");

        assertEquals(initialRowsNumCandidatesUsers + userEmails.size(), actualRowsNumCandidatesUsers);
    }

    @Test
    void canUpdateCandidateStatus() {
        Candidate candidate = TestDataBuilder.buildCandidate("Candidate1", "candidate1@yahoo.com");

        candidateRepository.updateCandidateStatus(candidate);

        var updatedStatus = candidate.getStatus();
        var expectedStatus = CandidateStatus.HIRED;

        assertEquals(expectedStatus, updatedStatus);
    }
}