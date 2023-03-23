package com.nagarro.candidatemanagement.mapper;

import com.nagarro.candidatemanagement.model.Feedback;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FeedbackRowMapper implements RowMapper<Feedback> {
    @Override
    public Feedback mapRow(ResultSet rs, int rowNum) throws SQLException {
        Feedback feedback = new Feedback();
        feedback.setComment(rs.getString("comment"));
        feedback.setCandidateId(rs.getLong("candidate_id"));
        feedback.setUserEmail(rs.getString("user_email"));
        feedback.setApproved(rs.getBoolean("status"));
        feedback.setDate(LocalDateTime.parse(rs.getString("date_time"), DateTimeFormatter.ISO_DATE_TIME));
        return feedback;
    }
}
