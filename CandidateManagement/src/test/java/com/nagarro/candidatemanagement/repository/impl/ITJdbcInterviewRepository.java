package com.nagarro.candidatemanagement.repository.impl;

import com.nagarro.candidatemanagement.model.Interview;
import com.nagarro.candidatemanagement.repository.InterviewRepository;
import com.nagarro.candidatemanagement.utils.TestDataBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@Sql({"classpath:script/drop_schema.sql", "classpath:script/create_schema.sql", "classpath:script/insertposition_schema.sql",
        "classpath:script/insertcandidate_schema.sql", "classpath:script/insertcandidateposition_schema.sql"})
public class ITJdbcInterviewRepository {

    @Autowired
    private InterviewRepository interviewRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testScheduleInterview() {
        Interview interview = TestDataBuilder.buildInterview(1L);
        int initialRowsNumber = JdbcTestUtils.countRowsInTable(jdbcTemplate, "interviews");

        Interview storedInterview = interviewRepository.addInterview(interview);

        int actualRowsNumber = JdbcTestUtils.countRowsInTable(jdbcTemplate, "interviews");

        assertEquals(interview.getId(), storedInterview.getId());
        assertEquals(initialRowsNumber + 1, actualRowsNumber);
    }
}
