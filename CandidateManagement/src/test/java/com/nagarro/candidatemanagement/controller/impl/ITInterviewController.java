package com.nagarro.candidatemanagement.controller.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nagarro.candidatemanagement.controller.dto.InterviewDTO;
import com.nagarro.candidatemanagement.utils.ApiTestUtils;
import com.nagarro.candidatemanagement.utils.TestDataBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 9999)
@Sql({"classpath:script/drop_schema.sql", "classpath:script/create_schema.sql", "classpath:script/insertposition_schema.sql",
        "classpath:script/insertcandidate_schema.sql", "classpath:script/insertcandidateposition_schema.sql",
        "classpath:script/insert_assigned_users.sql"})
public class ITInterviewController {
    private static final String TOKEN_HR_REQUEST = "Bearer OiJIUzUxMiJ9.eyJzdWIiOiJzdHJpbmcuc3RyaW5nQHlhaG9vLmNvbSIsInJvbGUiOiJIclJlcHJlc2VudGF0aXZlIiwiZXhwIjoxNjU5MTIwODM4LCJpYXQiOjE2NTkxMjAyMzh9.HG1jmVxuDv0eTXFV7KvgoJFAWZGN7j59VsYxEWZlP0MK6h0CWECf8yYqi0uCN7c-O3H6xAfVgN6SrH7cV-SM9A";

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ApiTestUtils apiTestUtils;

    @Test
    public void testScheduleInterview_shouldReturnStatusCode201() throws Exception {
        InterviewDTO interviewDTO = TestDataBuilder.buildInterviewDTO(1L);

        apiTestUtils.mockUserServiceResponse(HttpStatus.NO_CONTENT);
        apiTestUtils.mockUserEmailEndpointStatusCode200(true);

        mockMvc.perform(post("/interview")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(interviewDTO))
                        .header("Authorization", TOKEN_HR_REQUEST))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("candidateId").value(1))
                .andExpect(jsonPath("dateTime").exists())
                .andExpect(jsonPath("location").value("8 Broadway REDHILL RH2 4HT"));
    }

    @Test
    public void testScheduleInterview_whenNoAssignedUsers_shouldReturnStatusCode404() throws Exception {
        InterviewDTO interviewDTO = TestDataBuilder.buildInterviewDTO(2L);

        apiTestUtils.mockUserServiceResponse(HttpStatus.NO_CONTENT);

        mockMvc.perform(post("/interview")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(interviewDTO))
                        .header("Authorization", TOKEN_HR_REQUEST))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testScheduleInterview_whenInvalidCandidateId_shouldReturnStatusCode404() throws Exception {
        InterviewDTO interviewDTO = TestDataBuilder.buildInterviewDTO(10000L);

        apiTestUtils.mockUserServiceResponse(HttpStatus.NO_CONTENT);

        mockMvc.perform(post("/interview")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(interviewDTO))
                        .header("Authorization", TOKEN_HR_REQUEST))
                .andExpect(status().isNotFound());
    }
}
