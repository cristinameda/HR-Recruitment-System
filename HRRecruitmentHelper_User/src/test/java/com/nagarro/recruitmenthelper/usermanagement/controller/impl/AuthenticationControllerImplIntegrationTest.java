package com.nagarro.recruitmenthelper.usermanagement.controller.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nagarro.recruitmenthelper.usermanagement.controller.dto.AuthenticationRequestDTO;
import com.nagarro.recruitmenthelper.usermanagement.controller.dto.TokenDTO;
import com.nagarro.recruitmenthelper.usermanagement.controller.dto.UserDTO;
import com.nagarro.recruitmenthelper.usermanagement.domain.User;
import com.nagarro.recruitmenthelper.usermanagement.repository.UserRepository;
import com.nagarro.recruitmenthelper.usermanagement.tokenutils.TokenManager;
import com.nagarro.recruitmenthelper.usermanagement.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@Sql({"classpath:scripts/droptable.sql", "classpath:scripts/createtable.sql", "classpath:scripts/insertroles.sql"})
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class AuthenticationControllerImplIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenManager tokenManager;

    @Test
    void testCreateToken() throws Exception {
        User user = TestUtils.buildUser(1);
        UserDTO userDTO = TestUtils.buildUserDTO(1);
        userRepository.addUser(user);
        AuthenticationRequestDTO requestDTO = new AuthenticationRequestDTO(userDTO.getEmail(), userDTO.getPassword());

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("validity").value(600))
                .andExpect(jsonPath("token").exists());
    }

    @Test
    void testValidateToken_tokenIsInvalid() throws Exception {
        TokenDTO token = new TokenDTO("invalid.token");
        mockMvc.perform(MockMvcRequestBuilders.post("/validateToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(token)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testValidateToken_tokenValid() throws Exception {
        UserDTO userDTO = TestUtils.buildUserDTO();
        userRepository.addUser(TestUtils.buildUser());
        TokenDTO tokenDTO = new TokenDTO(tokenManager.generateToken(userDTO));
        mockMvc.perform(MockMvcRequestBuilders.post("/validateToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tokenDTO)))
                .andExpect(status().isNoContent());
    }

    @Test
    void testCreateToken_tokenIsExpired() throws Exception {
        UserDTO userDTO = TestUtils.buildUserDTO();
        userRepository.addUser(TestUtils.buildUser());

        TokenDTO tokenDTO = new TokenDTO(TestUtils.generateToken(userDTO));
        mockMvc.perform(MockMvcRequestBuilders.post("/validateToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tokenDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("The token has expired"))
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }
}