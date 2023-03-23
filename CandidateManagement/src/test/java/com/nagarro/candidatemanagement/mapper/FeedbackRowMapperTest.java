package com.nagarro.candidatemanagement.mapper;

import com.nagarro.candidatemanagement.model.Feedback;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FeedbackRowMapperTest {

    @Test
    void testMapRow() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        LocalDateTime dateTime = LocalDateTime.of(LocalDate.of(2022, 12, 10), LocalTime.of(12, 11, 12));
        Feedback feedback = new Feedback("comment", 1L, "email", false, dateTime);
        when(resultSet.getString("comment")).thenReturn("comment");
        when(resultSet.getLong("candidate_id")).thenReturn(1L);
        when(resultSet.getString("user_email")).thenReturn("email");
        when(resultSet.getBoolean("status")).thenReturn(false);
        when(resultSet.getString("date_time")).thenReturn(dateTime.toString());
        FeedbackRowMapper feedbackRowMapper = new FeedbackRowMapper();

        Feedback mappedFeedback = feedbackRowMapper.mapRow(resultSet, 1);

        assertEquals(feedback.getComment(), mappedFeedback.getComment());
        assertEquals(feedback.getCandidateId(), mappedFeedback.getCandidateId());
        assertEquals(feedback.getUserEmail(), mappedFeedback.getUserEmail());
        assertEquals(feedback.isApproved(), mappedFeedback.isApproved());
        assertEquals(feedback.getDate(), dateTime);
    }
}