package com.nagarro.candidatemanagement.repository.impl;

import com.nagarro.candidatemanagement.mapper.FeedbackRowMapper;
import com.nagarro.candidatemanagement.model.Feedback;
import com.nagarro.candidatemanagement.repository.FeedbackRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Repository
public class JdbcFeedbackRepository implements FeedbackRepository {

    private static final String INSERT_FEEDBACK_STATEMENT = "INSERT INTO feedback(comment, candidate_id, user_email, status, date_time) VALUES (?,?,?,?,?)";
    private static final String SELECT_FEEDBACK_BY_CANDIDATE_QUERY = "SELECT * FROM feedback WHERE candidate_id = ?";
    private final JdbcTemplate jdbcTemplate;

    public JdbcFeedbackRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addFeedback(Feedback feedback) {
        jdbcTemplate.update(INSERT_FEEDBACK_STATEMENT, feedback.getComment(), feedback.getCandidateId(),
                feedback.getUserEmail(), feedback.isApproved(), feedback.getDate().format(DateTimeFormatter.ISO_DATE_TIME));
    }

    @Override
    public List<Feedback> findByCandidateId(long candidateId) {
        return jdbcTemplate.query(SELECT_FEEDBACK_BY_CANDIDATE_QUERY, new FeedbackRowMapper(), candidateId);
    }
}

