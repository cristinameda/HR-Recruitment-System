package com.nagarro.candidatemanagement.mapper;

import com.nagarro.candidatemanagement.model.Candidate;
import com.nagarro.candidatemanagement.model.CandidateStatus;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class CandidateRowMapper implements RowMapper<Candidate> {

    @Override
    public Candidate mapRow(ResultSet rs, int rowNum) throws SQLException {
        Candidate candidate = new Candidate();
        candidate.setId(rs.getLong("candidate_id"));
        candidate.setName(rs.getString("name"));
        candidate.setPhoneNumber(rs.getString("phone_number"));
        candidate.setEmail(rs.getString("email"));
        candidate.setCity(rs.getString("city"));
        candidate.setExperienceYears(rs.getInt("experience_years"));
        candidate.setFaculty(rs.getString("faculty"));
        candidate.setRecruitmentChannel(rs.getString("recruitment_channel"));
        candidate.setBirthDate(LocalDate.parse(rs.getString("birth_date")));
        candidate.setStatus(CandidateStatus.valueOf(rs.getString("status")));
        return candidate;
    }
}
