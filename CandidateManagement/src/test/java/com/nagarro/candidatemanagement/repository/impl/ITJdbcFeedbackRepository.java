package com.nagarro.candidatemanagement.repository.impl;

import com.nagarro.candidatemanagement.model.Feedback;
import com.nagarro.candidatemanagement.repository.FeedbackRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@Sql({"classpath:script/drop_schema.sql", "classpath:script/create_schema.sql", "classpath:script/insertposition_schema.sql",
        "classpath:script/insertcandidate_schema.sql", "classpath:script/insertcandidateuser_schema.sql",
        "classpath:script/insertfile_schema.sql", "classpath:script/insertcandidateposition_schema.sql"})
public class ITJdbcFeedbackRepository {
    @Autowired
    FeedbackRepository feedbackRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testAddFeedback() {
        Feedback feedback = new Feedback("comment1", 1L, "user1@email.com", true, LocalDateTime.now());
        int initialRowsNumCandidatesUsers = JdbcTestUtils.countRowsInTable(jdbcTemplate, "feedback");

        feedbackRepository.addFeedback(feedback);

        int actualRowsNumCandidatesUsers = JdbcTestUtils.countRowsInTable(jdbcTemplate, "feedback");

        assertEquals(initialRowsNumCandidatesUsers + 1, actualRowsNumCandidatesUsers);
    }
}
