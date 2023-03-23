package com.nagarro.candidatemanagement.controller.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nagarro.candidatemanagement.controller.dto.FeedbackDTO;
import com.nagarro.candidatemanagement.model.Candidate;
import com.nagarro.candidatemanagement.model.Feedback;
import com.nagarro.candidatemanagement.repository.CandidateRepository;
import com.nagarro.candidatemanagement.repository.FeedbackRepository;
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

import java.time.LocalDateTime;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@AutoConfigureWireMock(port = 9999)
@Sql({"classpath:script/drop_schema.sql", "classpath:script/create_schema.sql", "classpath:script/insertposition_schema.sql"})
public class ITFeedbackController {
    @Autowired
    private CandidateRepository candidateRepository;
    @Autowired
    private FeedbackRepository feedbackRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ApiTestUtils apiTestUtils;

    @Test
    void testGiveFeedbackToCandidate_shouldReturnStatusCode200() throws Exception {
        FeedbackDTO feedbackDTO = new FeedbackDTO("comment", null, null, true, null);
        Candidate candidate = candidateRepository.save(TestDataBuilder.buildCandidate("name", "email"));
        candidate.setAssignedUsers(List.of("test@yahoo.com"));
        candidateRepository.updateCandidateAssignedUsers(candidate);
        apiTestUtils.mockUserServiceResponse(HttpStatus.NO_CONTENT);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        mockMvc.perform(post("/feedback/" + candidate.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(feedbackDTO))
                        .header("Authorization", TestDataBuilder.buildHrRepresentativeToken()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.candidateId").value(1))
                .andExpect(jsonPath("$.userEmail").value("test@yahoo.com"))
                .andExpect(jsonPath("$.date").exists());

        verify(postRequestedFor(urlPathEqualTo(apiTestUtils.getTokenValidationEndpoint())));
    }

    @Test
    void testGiveFeedbackToCandidate_whenCandidateNotFound_shouldReturnStatusCode404() throws Exception {
        FeedbackDTO feedbackDTO = new FeedbackDTO("comment", null, null, true, null);
        apiTestUtils.mockUserServiceResponse(HttpStatus.NO_CONTENT);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        mockMvc.perform(post("/feedback/222")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(feedbackDTO))
                        .header("Authorization", TestDataBuilder.buildHrRepresentativeToken()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Candidate with id: 222 wasn't found!"));

        verify(postRequestedFor(urlPathEqualTo(apiTestUtils.getTokenValidationEndpoint())));
    }

    @Test
    void testGiveFeedbackToCandidate_whenUserIsNotAssigned_shouldReturnStatusCode400() throws Exception {
        FeedbackDTO feedbackDTO = new FeedbackDTO("comment", null, null, true, null);
        Candidate candidate = candidateRepository.save(TestDataBuilder.buildCandidate("name", "email"));
        apiTestUtils.mockUserServiceResponse(HttpStatus.NO_CONTENT);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        mockMvc.perform(post("/feedback/" + candidate.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(feedbackDTO))
                        .header("Authorization", TestDataBuilder.buildHrRepresentativeToken()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("You are not assigned to candidate 'email'!"));

        verify(postRequestedFor(urlPathEqualTo(apiTestUtils.getTokenValidationEndpoint())));
    }


    @Test
    void testGiveFeedbackToCandidate_whenUserAlreadyGaveFeedback_shouldReturnStatusCode409() throws Exception {
        FeedbackDTO feedbackDTO = new FeedbackDTO("comment", null, null, true, null);
        Candidate candidate = candidateRepository.save(TestDataBuilder.buildCandidate("name", "email"));
        candidate.setAssignedUsers(List.of("test@yahoo.com"));
        candidateRepository.updateCandidateAssignedUsers(candidate);
        feedbackRepository.addFeedback(new Feedback("comment", candidate.getId(), "test@yahoo.com", true, LocalDateTime.now()));

        apiTestUtils.mockUserServiceResponse(HttpStatus.NO_CONTENT);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        mockMvc.perform(post("/feedback/" + candidate.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(feedbackDTO))
                        .header("Authorization", TestDataBuilder.buildHrRepresentativeToken()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("You already gave feedback to candidate 'email'!"));

        verify(postRequestedFor(urlPathEqualTo(apiTestUtils.getTokenValidationEndpoint())));
    }

    @Test
    void testGiveFeedbackToCandidate_whenUserIsUnauthorized_shouldReturnStatusCode401() throws Exception {
        FeedbackDTO feedbackDTO = new FeedbackDTO("comment", null, null, true, null);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        apiTestUtils.mockUserServiceResponse(HttpStatus.UNAUTHORIZED);

        mockMvc.perform(post("/feedback/222")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(feedbackDTO))
                        .header("Authorization", TestDataBuilder.buildHrRepresentativeToken()))
                .andExpect(status().isUnauthorized());

        verify(postRequestedFor(urlPathEqualTo(apiTestUtils.getTokenValidationEndpoint())));
    }
}
