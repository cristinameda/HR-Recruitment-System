package com.nagarro.candidatemanagement.controller.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nagarro.candidatemanagement.controller.dto.CandidateDTO;
import com.nagarro.candidatemanagement.controller.dto.UpdateCandidateStatusDTO;
import com.nagarro.candidatemanagement.controller.dto.UserEmailRoleDTO;
import com.nagarro.candidatemanagement.model.Candidate;
import com.nagarro.candidatemanagement.model.CandidateStatus;
import com.nagarro.candidatemanagement.model.File;
import com.nagarro.candidatemanagement.model.FileType;
import com.nagarro.candidatemanagement.repository.CandidateRepository;
import com.nagarro.candidatemanagement.utils.ApiTestUtils;
import com.nagarro.candidatemanagement.utils.TestDataBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@AutoConfigureWireMock(port = 9999)
@Sql({"classpath:script/drop_schema.sql", "classpath:script/create_schema.sql", "classpath:script/insertposition_schema.sql",
        "classpath:script/insertcandidate_schema.sql", "classpath:script/insertcandidateposition_schema.sql",
        "classpath:script/insert_assigned_users.sql"})
class ITCandidateController {
    private static final String TOKEN_HR_REQUEST = "Bearer OiJIUzUxMiJ9.eyJzdWIiOiJzdHJpbmcuc3RyaW5nQHlhaG9vLmNvbSIsInJvbGUiOiJIclJlcHJlc2VudGF0aXZlIiwiZXhwIjoxNjU5MTIwODM4LCJpYXQiOjE2NTkxMjAyMzh9.HG1jmVxuDv0eTXFV7KvgoJFAWZGN7j59VsYxEWZlP0MK6h0CWECf8yYqi0uCN7c-O3H6xAfVgN6SrH7cV-SM9A";
    private static final String TOKEN_ADMIN_REQUEST = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMUB5YWhvby5jb20iLCJyb2xlIjoiQWRtaW4iLCJleHAiOjE2NTkxMjA0OTMsImlhdCI6MTY1OTExOTg5M30.HW_lkdz1QnUClQ1CI0I_AOmOwhP8m2xzZS26ZGHGHITKeuS__sw1hTayUgDeZjh0YounYLlUFq3RJ4LmId7wsQ";
    private final static String TOKEN_TI_REQUEST = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMkB5YWhvby5jb20iLCJyb2xlIjoiVGVobmljYWxJbnRlcnZpZXdlciIsImV4cCI6MTY1OTEyMTE2OSwiaWF0IjoxNjU5MTIwNTY5fQ.mTypjd0qcdKMYhcl3kKkU9B_xn9WXxs9k-t-x_2GDe070j_fq8e86eLgsnfxXFH4-mshYZh1EiH58kICZsO7RQ";
    private final static String TOKEN_PTE_REQUEST = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyM0B5YWhvby5jb20iLCJyb2xlIjoiUFRFIiwiZXhwIjoxNjU5MTIxMzM1LCJpYXQiOjE2NTkxMjA3MzV9.5Nt0EEr4tbRfrl5o8xEJBqf0BtlLl4bICLUmmZ5ETYvPNlJ_iYG_A9STz2P2YxcGlc2f4jfx004LeeXyyf5RnQ";
    private final static String HIRED_STATUS = CandidateStatus.HIRED.name();
    private final static String NO_STATUS = CandidateStatus.NO_STATUS.name();

    @Autowired
    private CandidateRepository candidateRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ApiTestUtils apiTestUtils;

    private static Stream<Arguments> candidateFieldsProvider() {
        return Stream.of(
                Arguments.of("name", null),
                Arguments.of("phoneNumber", null),
                Arguments.of("phoneNumber", "12345678"),
                Arguments.of("email", null),
                Arguments.of("email", "a-b*c@abc.com"),
                Arguments.of("city", null),
                Arguments.of("experienceYears", -1),
                Arguments.of("faculty", null),
                Arguments.of("recruitmentChannel", null),
                Arguments.of("birthDate", null),
                Arguments.of("birthDate", LocalDate.of(2010, 2, 1)),
                Arguments.of("interestedPositionsDTO", null),
                Arguments.of("status", null));
    }

    @Test
    void testAddValidCandidate() throws Exception {
        CandidateDTO candidateDTO = TestDataBuilder.buildCandidateDTO(7L, "Candidate7", "candidate7@yahoo.com");
        apiTestUtils.mockUserServiceResponse(HttpStatus.NO_CONTENT);

        MockMultipartFile firstFile = new MockMultipartFile("CV", "filename.txt", "text/plain", "some xml".getBytes());
        MockMultipartFile secondFile = new MockMultipartFile("GDPR", "other-file-name.data", "text/plain", "some other type".getBytes());
        MockMultipartFile jsonFile = new MockMultipartFile("candidate", "", "application/json", objectMapper.writeValueAsString(candidateDTO).getBytes());

        mockMvc.perform(multipart("/candidates")
                        .file(firstFile)
                        .file(secondFile)
                        .file(jsonFile)
                        .header("Authorization", TOKEN_HR_REQUEST))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").value("Candidate7"))
                .andExpect(jsonPath("phoneNumber").value("0735456654"))
                .andExpect(jsonPath("email").value("candidate7@yahoo.com"))
                .andExpect(jsonPath("city").value("Cluj"))
                .andExpect(jsonPath("experienceYears").value(4))
                .andExpect(jsonPath("faculty").value("Computer Science"))
                .andExpect(jsonPath("recruitmentChannel").value("Facebook"))
                .andExpect(jsonPath("birthDate").value("2001-03-03"))
                .andExpect(jsonPath("$.interestedPositions[0].id").value(1L))
                .andExpect(jsonPath("status").value(NO_STATUS))
                .andExpect(jsonPath("$.files[0].id").value(1L))
                .andExpect(jsonPath("$.files[1].id").value(2L));

        verify(postRequestedFor(urlPathEqualTo(apiTestUtils.getTokenValidationEndpoint())));
    }

    @Test
    void testAddCandidate_shouldReturnUnauthorized() throws Exception {
        CandidateDTO candidateDTO = TestDataBuilder.buildCandidateDTO(7L, "Candidate7", "candidate7@yahoo.com");
        apiTestUtils.mockUserServiceResponse(HttpStatus.NO_CONTENT);

        mockMvc.perform(post("/candidates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(candidateDTO))
                        .header("Authorization", TOKEN_ADMIN_REQUEST))
                .andExpect(status().isUnauthorized());

        verify(postRequestedFor(urlPathEqualTo(apiTestUtils.getTokenValidationEndpoint())));
    }

    @Test
    void testFindAll_whenHrRepresentativeNoFilter_shouldReturnAllCandidates() throws Exception {
        apiTestUtils.mockUserServiceResponse(HttpStatus.NO_CONTENT);

        mockMvc.perform(MockMvcRequestBuilders.get("/candidates")
                        .param("pageNo", "2")
                        .param("pageSize", "2")
                        .header("Authorization", TestDataBuilder.buildHrRepresentativeToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].feedback").exists())
                .andExpect(jsonPath("$[1].id").exists())
                .andExpect(jsonPath("$[1].feedback").exists());

        verify(postRequestedFor(urlPathEqualTo(apiTestUtils.getTokenValidationEndpoint())));
    }

    @Test
    void testFindAll_whenPTENoFilter_shouldReturnAllCandidates() throws Exception {
        apiTestUtils.mockUserServiceResponse(HttpStatus.NO_CONTENT);

        mockMvc.perform(MockMvcRequestBuilders.get("/candidates")
                        .param("pageNo", "2")
                        .param("pageSize", "2")
                        .header("Authorization", TestDataBuilder.buildPTEToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].feedback").exists())
                .andExpect(jsonPath("$[1].id").exists())
                .andExpect(jsonPath("$[1].feedback").exists());

        verify(postRequestedFor(urlPathEqualTo(apiTestUtils.getTokenValidationEndpoint())));
    }

    @Test
    void testFindAll_whenFilterByEmail_shouldReturnCandidatesFilteredByEmail() throws Exception {
        apiTestUtils.mockUserServiceResponse(HttpStatus.NO_CONTENT);

        mockMvc.perform(MockMvcRequestBuilders.get("/candidates")
                        .param("pageNo", "1")
                        .param("pageSize", "16")
                        .param("field", "email")
                        .param("filterValue", "ate3")
                        .header("Authorization", TestDataBuilder.buildHrRepresentativeToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("candidate3@yahoo.com"));

        verify(postRequestedFor(urlPathEqualTo(apiTestUtils.getTokenValidationEndpoint())));
    }

    @Test
    void testFindAll_whenFilterByName_shouldReturnCandidatesFilteredByName() throws Exception {
        apiTestUtils.mockUserServiceResponse(HttpStatus.NO_CONTENT);

        mockMvc.perform(MockMvcRequestBuilders.get("/candidates")
                        .param("pageNo", "1")
                        .param("pageSize", "16")
                        .param("field", "name")
                        .param("filterValue", "ate3")
                        .header("Authorization", TestDataBuilder.buildHrRepresentativeToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Candidate3"));

        verify(postRequestedFor(urlPathEqualTo(apiTestUtils.getTokenValidationEndpoint())));
    }

    @Test
    void testFindAll_whenFilterFieldIsInvalid_shouldReturnStatusCode400() throws Exception {
        apiTestUtils.mockUserServiceResponse(HttpStatus.NO_CONTENT);

        mockMvc.perform(MockMvcRequestBuilders.get("/candidates")
                        .param("pageNo", "1")
                        .param("pageSize", "16")
                        .param("field", "invalid_field")
                        .param("filterValue", "ate1")
                        .header("Authorization", TestDataBuilder.buildHrRepresentativeToken()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Cannot filter candidates by field 'invalid_field'!"));

        verify(postRequestedFor(urlPathEqualTo(apiTestUtils.getTokenValidationEndpoint())));
    }

    @Test
    @Rollback
    public void testFindAll_shouldReturnUnauthorized() throws Exception {
        apiTestUtils.mockUserServiceResponse(HttpStatus.UNAUTHORIZED);

        mockMvc.perform(MockMvcRequestBuilders.get("/candidates")
                        .param("pageNo", "2")
                        .param("pageSize", "2")
                        .header("Authorization", "Bearer invalid_token"))
                .andExpect(status().isUnauthorized());

        verify(postRequestedFor(urlPathEqualTo(apiTestUtils.getTokenValidationEndpoint())));
    }

    @Test
    public void testFindAll_whenTechInterviewer_shouldReturnAssignedCandidates() throws Exception {
        apiTestUtils.mockUserServiceResponse(HttpStatus.NO_CONTENT);
        Candidate candidate = TestDataBuilder.buildCandidate("candidate", "candidate@yahoo.com");
        candidate.setFiles(List.of(new File("test", FileType.CV, "hello".getBytes()), new File("test", FileType.GDPR, "hello".getBytes())));
        candidateRepository.save(candidate);
        candidate.setAssignedUsers(List.of("anotherTest@yahoo.com"));
        candidateRepository.updateCandidateAssignedUsers(candidate);

        mockMvc.perform(MockMvcRequestBuilders.get("/candidates")
                        .param("pageNo", "1")
                        .param("pageSize", "2")
                        .header("Authorization", TestDataBuilder.buildTechnicalInterviewerToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("candidate@yahoo.com"));

        verify(postRequestedFor(urlPathEqualTo(apiTestUtils.getTokenValidationEndpoint())));
    }

    @Test
    public void testFindById_shouldReturnRequestedCandidate() throws Exception {
        apiTestUtils.mockUserServiceResponse(HttpStatus.NO_CONTENT);

        mockMvc.perform(MockMvcRequestBuilders.get("/candidates/2")
                        .header("Authorization", TOKEN_HR_REQUEST))
                .andExpect(jsonPath("id").value(2))
                .andExpect(jsonPath("name").value("Candidate2"))
                .andExpect(jsonPath("phoneNumber").value("0712345678"))
                .andExpect(jsonPath("email").value("candidate2@yahoo.com"))
                .andExpect(jsonPath("city").value("Cluj"))
                .andExpect(jsonPath("experienceYears").value(2))
                .andExpect(jsonPath("faculty").value("Computer Science"))
                .andExpect(jsonPath("recruitmentChannel").value("Facebook"))
                .andExpect(jsonPath("birthDate").value("1999-02-23"))
                .andExpect(jsonPath("$.interestedPositions[0].id").exists())
                .andExpect(jsonPath("$.feedback").exists());

        verify(postRequestedFor(urlPathEqualTo(apiTestUtils.getTokenValidationEndpoint())));
    }

    @Test
    public void findById_shouldReturnUnauthorized() throws Exception {
        apiTestUtils.mockUserServiceResponse(HttpStatus.UNAUTHORIZED);

        mockMvc.perform(MockMvcRequestBuilders.get("/candidates/2")
                        .header("Authorization", "Bearer valid_token"))
                .andExpect(status().isUnauthorized());

        verify(postRequestedFor(urlPathEqualTo(apiTestUtils.getTokenValidationEndpoint())));
    }

    @Test
    void testDeleteCandidate() throws Exception {
        apiTestUtils.mockUserServiceResponse(HttpStatus.NO_CONTENT);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/candidates/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", TOKEN_ADMIN_REQUEST))
                .andExpect(status().isNoContent());

        verify(postRequestedFor(urlPathEqualTo(apiTestUtils.getTokenValidationEndpoint())));
    }

    @Test
    @Rollback
    void testDeleteCandidate_shouldReturnUnauthorized() throws Exception {
        apiTestUtils.mockUserServiceResponse(HttpStatus.UNAUTHORIZED);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/candidates/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer valid_token"))
                .andExpect(status().isUnauthorized());

        verify(postRequestedFor(urlPathEqualTo(apiTestUtils.getTokenValidationEndpoint())));
    }

    @Test
    public void testDeleteCandidate_shouldReturnNotFoundResponse() throws Exception {
        apiTestUtils.mockUserServiceResponse(HttpStatus.NO_CONTENT);

        mockMvc.perform(delete("/candidates/222")
                        .header("Authorization", TOKEN_ADMIN_REQUEST))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Candidate with id 222 does not exist!"));

        verify(postRequestedFor(urlPathEqualTo(apiTestUtils.getTokenValidationEndpoint())));
    }

    @Test
    void findByIdCandidate_shouldReturnNotFoundResponse() throws Exception {
        apiTestUtils.mockUserServiceResponse(HttpStatus.NO_CONTENT);

        mockMvc.perform(MockMvcRequestBuilders.get("/candidates/222")
                        .header("Authorization", TOKEN_HR_REQUEST))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Candidate with id: 222 wasn't found!"));

        verify(postRequestedFor(urlPathEqualTo(apiTestUtils.getTokenValidationEndpoint())));
    }

    @Test
    @Rollback
    public void testFindByIdCandidate_shouldReturnUnauthorized() throws Exception {
        apiTestUtils.mockUserServiceResponse(HttpStatus.UNAUTHORIZED);

        mockMvc.perform(delete("/candidates/222")
                        .header("Authorization", "Bearer valid_token"))
                .andExpect(status().isUnauthorized());

        verify(postRequestedFor(urlPathEqualTo(apiTestUtils.getTokenValidationEndpoint())));
    }


    @ParameterizedTest
    @MethodSource("candidateFieldsProvider")
    void testAddInvalidCandidate_validFiles(String fieldName, Object invalidValue) throws Exception {
        CandidateDTO candidateDTO = TestDataBuilder.buildCandidateDTO(7L, "Candidate1", "candidate1@yahoo.com");
        Field field = candidateDTO.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(candidateDTO, invalidValue);

        apiTestUtils.mockUserServiceResponse(HttpStatus.NO_CONTENT);
        MockMultipartFile firstFile = new MockMultipartFile("CV", "filename.txt", "text/plain", "some xml".getBytes());
        MockMultipartFile secondFile = new MockMultipartFile("GDPR", "other-file-name.data", "text/plain", "some other type".getBytes());
        MockMultipartFile jsonFile = new MockMultipartFile("candidate", "", "application/json", objectMapper.writeValueAsString(candidateDTO).getBytes());

        mockMvc.perform(multipart("/candidates")
                        .file(firstFile)
                        .file(secondFile)
                        .file(jsonFile)
                        .header("Authorization", TOKEN_HR_REQUEST))

                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.httpStatus").exists());

        verify(postRequestedFor(urlPathEqualTo(apiTestUtils.getTokenValidationEndpoint())));
    }

    @Test
    void testAddInvalidCandidate_noFileProvided() throws Exception {
        CandidateDTO candidateDTO = TestDataBuilder.buildCandidateDTO(7L, "Candidate1", "candidate100@yahoo.com");
        MockMultipartFile jsonFile = new MockMultipartFile("candidate", "", "application/json", objectMapper.writeValueAsString(candidateDTO).getBytes());
        apiTestUtils.mockUserServiceResponse(HttpStatus.NO_CONTENT);

        mockMvc.perform(multipart("/candidates")
                        .file(jsonFile)
                        .header("Authorization", TOKEN_HR_REQUEST))
                .andExpect(status().isBadRequest());
        verify(postRequestedFor(urlPathEqualTo(apiTestUtils.getTokenValidationEndpoint())));
    }

    @ParameterizedTest
    @ValueSource(strings = {TOKEN_ADMIN_REQUEST, TOKEN_PTE_REQUEST, TOKEN_TI_REQUEST})
    void testAddCandidate_shouldReturnUnauthorized(String token) throws Exception {
        CandidateDTO candidateDTO = TestDataBuilder.buildCandidateDTO(7L, "Candidate7", "candidate7@yahoo.com");
        apiTestUtils.mockUserServiceResponse(HttpStatus.NO_CONTENT);

        mockMvc.perform(post("/candidates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(candidateDTO))
                        .header("Authorization", token))
                .andExpect(status().isUnauthorized());

        verify(postRequestedFor(urlPathEqualTo(apiTestUtils.getTokenValidationEndpoint())));
    }

    @ParameterizedTest
    @ValueSource(strings = {TOKEN_PTE_REQUEST, TOKEN_HR_REQUEST, TOKEN_TI_REQUEST})
    void deleteCandidate_shouldReturnUnauthorized(String token) throws Exception {
        apiTestUtils.mockUserServiceResponse(HttpStatus.UNAUTHORIZED);

        mockMvc.perform(delete("/candidates/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", token))
                .andExpect(status().isUnauthorized());

        verify(postRequestedFor(urlPathEqualTo(apiTestUtils.getTokenValidationEndpoint())));
    }

    @ParameterizedTest
    @ValueSource(strings = {TOKEN_ADMIN_REQUEST, TOKEN_PTE_REQUEST, TOKEN_TI_REQUEST})
    void findAll_shouldReturnUnauthorized(String token) throws Exception {
        apiTestUtils.mockUserServiceResponse(HttpStatus.UNAUTHORIZED);

        mockMvc.perform(MockMvcRequestBuilders.get("/candidates")
                        .param("pageNo", "2")
                        .param("pageSize", "2")
                        .header("Authorization", token))
                .andExpect(status().isUnauthorized());

        verify(postRequestedFor(urlPathEqualTo(apiTestUtils.getTokenValidationEndpoint())));
    }

    @ParameterizedTest
    @ValueSource(strings = {TOKEN_ADMIN_REQUEST, TOKEN_PTE_REQUEST, TOKEN_TI_REQUEST})
    void findById_shouldReturnUnauthorized(String token) throws Exception {
        apiTestUtils.mockUserServiceResponse(HttpStatus.UNAUTHORIZED);

        mockMvc.perform(delete("/candidates/222")
                        .header("Authorization", token))
                .andExpect(status().isUnauthorized());

        verify(postRequestedFor(urlPathEqualTo(apiTestUtils.getTokenValidationEndpoint())));
    }

    @Test
    void testAssignUsersToCandidate_shouldReturnStatusCode204() throws Exception {
        List<UserEmailRoleDTO> usersToBeAssigned = TestDataBuilder.generateUserEmailRoleDTOListWithRequiredRoles();
        apiTestUtils.mockUserServiceResponse(HttpStatus.NO_CONTENT);
        apiTestUtils.mockUserEmailEndpointStatusCode200(true);

        mockMvc.perform(put("/candidates/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usersToBeAssigned))
                        .header("Authorization", TOKEN_HR_REQUEST))
                .andExpect(status().isNoContent());
    }

    @Test
    void testAssignUsersToCandidate_whenInvalidUsers_shouldReturnStatusCode400() throws Exception {
        List<UserEmailRoleDTO> usersToBeAssigned = TestDataBuilder.generateUserEmailRoleDTOListWithRequiredRoles();
        apiTestUtils.mockUserServiceResponse(HttpStatus.NO_CONTENT);
        apiTestUtils.mockUserEmailEndpointStatusCode200(false);

        mockMvc.perform(put("/candidates/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usersToBeAssigned))
                        .header("Authorization", TOKEN_HR_REQUEST))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAssignUsersToCandidate_whenTooManyUserRoles_shouldReturnStatusCode400() throws Exception {
        List<UserEmailRoleDTO> usersToBeAssigned = TestDataBuilder.generateUserEmailRoleDTOListWithRequiredRoles();
        usersToBeAssigned.add(new UserEmailRoleDTO("hellokitty@yahoo.com", "HrRepresentative"));
        apiTestUtils.mockUserServiceResponse(HttpStatus.NO_CONTENT);

        mockMvc.perform(put("/candidates/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usersToBeAssigned))
                        .header("Authorization", TOKEN_HR_REQUEST))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAssignUsersToCandidate_whenTooFewUserRoles_shouldReturnStatusCode400() throws Exception {
        List<UserEmailRoleDTO> usersToBeAssigned = TestDataBuilder.generateUserEmailRoleDTOListWithRequiredRoles();
        usersToBeAssigned.remove(0);
        apiTestUtils.mockUserServiceResponse(HttpStatus.NO_CONTENT);

        mockMvc.perform(put("/candidates/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usersToBeAssigned))
                        .header("Authorization", TOKEN_HR_REQUEST))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAssignUsersToCandidate_whenUserEndpointBadRequest_shouldReturnStatusCode400() throws Exception {
        List<UserEmailRoleDTO> usersToBeAssigned = TestDataBuilder.generateUserEmailRoleDTOListWithRequiredRoles();
        apiTestUtils.mockUserServiceResponse(HttpStatus.NO_CONTENT);
        apiTestUtils.mockUserEmailEndpointStatusCode400();

        mockMvc.perform(put("/candidates/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usersToBeAssigned))
                        .header("Authorization", TOKEN_HR_REQUEST))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAssignUsersToCandidate_whenUserNotFound_shouldReturnStatusCode404() throws Exception {
        List<UserEmailRoleDTO> usersToBeAssigned = TestDataBuilder.generateUserEmailRoleDTOListWithRequiredRoles();
        apiTestUtils.mockUserServiceResponse(HttpStatus.NO_CONTENT);

        mockMvc.perform(put("/candidates/100000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usersToBeAssigned))
                        .header("Authorization", TOKEN_HR_REQUEST))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAssignUsersToCandidate_shouldReturnUnauthorized() throws Exception {
        List<UserEmailRoleDTO> usersToBeAssigned = TestDataBuilder.generateUserEmailRoleDTOListWithRequiredRoles();
        apiTestUtils.mockUserServiceResponse(HttpStatus.UNAUTHORIZED);

        mockMvc.perform(put("/candidates/100000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usersToBeAssigned))
                        .header("Authorization", TOKEN_HR_REQUEST))
                .andExpect(status().isUnauthorized());

        verify(postRequestedFor(urlPathEqualTo(apiTestUtils.getTokenValidationEndpoint())));
    }

    @ParameterizedTest
    @ValueSource(strings = {TOKEN_HR_REQUEST, TOKEN_PTE_REQUEST})
    void testAssignStatusToCandidate_shouldReturnStatusCode204(String token) throws Exception {
        UpdateCandidateStatusDTO updateCandidateStatusDTO = TestDataBuilder.generateRequiredCandidateAndStatus(HIRED_STATUS);
        apiTestUtils.mockUserServiceResponse(HttpStatus.NO_CONTENT);

        mockMvc.perform(put("/candidates/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCandidateStatusDTO))
                        .header("Authorization", token))
                .andExpect(status().isNoContent());

        verify(postRequestedFor(urlPathEqualTo(apiTestUtils.getTokenValidationEndpoint())));
    }

    @ParameterizedTest
    @ValueSource(strings = {TOKEN_ADMIN_REQUEST, TOKEN_TI_REQUEST})
    void testAssignStatusToCandidate_shouldReturnUnauthorized(String token) throws Exception {
        UpdateCandidateStatusDTO updateCandidateStatusDTO = TestDataBuilder.generateRequiredCandidateAndStatus(HIRED_STATUS);
        apiTestUtils.mockUserServiceResponse(HttpStatus.UNAUTHORIZED);

        mockMvc.perform(put("/candidates/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCandidateStatusDTO))
                        .header("Authorization", token))
                .andExpect(status().isUnauthorized());

        verify(postRequestedFor(urlPathEqualTo(apiTestUtils.getTokenValidationEndpoint())));
    }

    @Test
    void testAddInvalidCandidate_noContentFiles() throws Exception {
        CandidateDTO candidateDTO = TestDataBuilder.buildCandidateDTO(7L, "Candidate101", "candidate101@yahoo.com");
        MockMultipartFile firstFile = new MockMultipartFile("CV", "filename.txt", "text/plain", "".getBytes());
        MockMultipartFile secondFile = new MockMultipartFile("GDPR", "other-file-name.data", "text/plain", "".getBytes());
        MockMultipartFile jsonFile = new MockMultipartFile("candidate", "", "application/json", objectMapper.writeValueAsString(candidateDTO).getBytes());
        apiTestUtils.mockUserServiceResponse(HttpStatus.NO_CONTENT);

        mockMvc.perform(multipart("/candidates")
                        .file(firstFile)
                        .file(secondFile)
                        .file(jsonFile)
                        .header("Authorization", TOKEN_HR_REQUEST))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.httpStatus").exists());

        verify(postRequestedFor(urlPathEqualTo(apiTestUtils.getTokenValidationEndpoint())));
    }
}