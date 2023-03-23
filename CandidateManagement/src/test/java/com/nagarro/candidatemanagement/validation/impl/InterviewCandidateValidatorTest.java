package com.nagarro.candidatemanagement.validation.impl;

import com.nagarro.candidatemanagement.exception.CandidateNotFoundException;
import com.nagarro.candidatemanagement.validation.CandidateValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@Sql({"classpath:script/drop_schema.sql", "classpath:script/create_schema.sql", "classpath:script/insertposition_schema.sql",
        "classpath:script/insertcandidate_schema.sql", "classpath:script/insertcandidateposition_schema.sql",
        "classpath:script/insert_assigned_users.sql"})
public class InterviewCandidateValidatorTest {
    @Autowired
    private CandidateValidator candidateValidator;

    @Test
    public void testValidateCandidate() {
        assertDoesNotThrow(() -> candidateValidator.validateCandidate(1L));
    }

    @Test
    public void testValidateCandidate_whenIdNotFound_shouldThrowCandidateNotFoundException() {
        CandidateNotFoundException exception = assertThrows(CandidateNotFoundException.class, () ->
                candidateValidator.validateCandidate(10000L));
        assertEquals("Invalid candidate with id: '10000' selected for interview!", exception.getMessage());
    }

    @Test
    public void testValidateCandidate_whenNotAssignedUsers_shouldThrowCandidateNotFoundException() {
        CandidateNotFoundException exception = assertThrows(CandidateNotFoundException.class, () ->
                candidateValidator.validateCandidate(2L));
        assertEquals("Invalid candidate with id: '2' selected for interview!", exception.getMessage());
    }
}
