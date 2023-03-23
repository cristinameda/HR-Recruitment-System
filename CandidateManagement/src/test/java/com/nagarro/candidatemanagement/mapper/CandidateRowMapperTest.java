package com.nagarro.candidatemanagement.mapper;

import com.nagarro.candidatemanagement.model.Candidate;
import com.nagarro.candidatemanagement.model.CandidateStatus;
import com.nagarro.candidatemanagement.utils.TestDataBuilder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CandidateRowMapperTest {

    @Test
    void CandidateRowMapper() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        Candidate candidate = TestDataBuilder.buildCandidate("Candidate1", "candidate1@yahoo.com");
        candidate.setId(1L);
        when(resultSet.getLong("candidate_id")).thenReturn(1L);
        when(resultSet.getString("name")).thenReturn("Candidate1");
        when(resultSet.getString("phone_number")).thenReturn("0735456654");
        when(resultSet.getString("email")).thenReturn("candidate1@yahoo.com");
        when(resultSet.getString("city")).thenReturn("Cluj");
        when(resultSet.getInt("experience_years")).thenReturn(4);
        when(resultSet.getString("faculty")).thenReturn("Computer Science");
        when(resultSet.getString("recruitment_channel")).thenReturn("Facebook");
        when(resultSet.getString("birth_date")).thenReturn(String.valueOf(LocalDate.of(2001, 3, 3)));
        when(resultSet.getString("status")).thenReturn(CandidateStatus.NO_STATUS.name());

        CandidateRowMapper candidateRowMapper = new CandidateRowMapper();
        Candidate mappedCandidate = candidateRowMapper.mapRow(resultSet, 1);
        mappedCandidate.setInterestedPositions(candidate.getInterestedPositions());

        assertEquals(candidate.getId(), mappedCandidate.getId());
    }
}