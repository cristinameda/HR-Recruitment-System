package com.nagarro.candidatemanagement.repository.impl;

import com.nagarro.candidatemanagement.model.Interview;
import com.nagarro.candidatemanagement.repository.InterviewRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

@Repository
public class JdbcInterviewRepository implements InterviewRepository {

    private static final String INSERT_INTERVIEW_STATEMENT = "INSERT INTO interviews(candidate_id, date_time, location) VALUES(?, ?, ?)";

    private final JdbcTemplate jdbcTemplate;

    public JdbcInterviewRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Interview addInterview(Interview interview) {
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(con -> getPreparedStatement(interview, con), keyHolder);
            Optional.ofNullable(keyHolder.getKeys())
                    .ifPresent(keys -> interview.setId((long) keys.get("interview_id")));
            return interview;
        } catch (DataAccessException e) {
            return null;
        }
    }

    private PreparedStatement getPreparedStatement(Interview interview, Connection con) throws SQLException {
        PreparedStatement ps = con.prepareStatement(INSERT_INTERVIEW_STATEMENT, Statement.RETURN_GENERATED_KEYS);
        ps.setLong(1, interview.getCandidateId());
        ps.setString(2, interview.getDateTime().toString());
        ps.setString(3, interview.getLocation());
        return ps;
    }
}
