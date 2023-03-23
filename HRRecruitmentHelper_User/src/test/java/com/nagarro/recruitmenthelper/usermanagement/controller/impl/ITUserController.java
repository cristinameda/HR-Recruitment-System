package com.nagarro.recruitmenthelper.usermanagement.controller.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nagarro.recruitmenthelper.usermanagement.controller.dto.UserDTO;
import com.nagarro.recruitmenthelper.usermanagement.controller.dto.UserEmailRoleDTO;
import com.nagarro.recruitmenthelper.usermanagement.domain.User;
import com.nagarro.recruitmenthelper.usermanagement.repository.UserRepository;
import com.nagarro.recruitmenthelper.usermanagement.service.security.PasswordHashGenerator;
import com.nagarro.recruitmenthelper.usermanagement.tokenutils.TokenManager;
import com.nagarro.recruitmenthelper.usermanagement.utils.TestUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Sql({"classpath:scripts/droptable.sql", "classpath:scripts/createtable.sql", "classpath:scripts/insertroles.sql"})
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ITUserController {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenManager tokenManager;
    private String token;
    @Autowired
    private PasswordHashGenerator passwordHashGenerator;

    @Test
    @Rollback
    public void testAddUser_shouldRespondWithStatusCode201() throws Exception {
        UserDTO userDTO = TestUtils.buildUserDTO();
        User user = TestUtils.buildUser();
        userRepository.addUser(user);
        token = tokenManager.generateToken(userDTO);
        UserDTO userDTO1 = TestUtils.buildUserDTO(1);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO1))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email").value("user1@email.com"))
                .andExpect(jsonPath("password").exists())
                .andExpect(jsonPath("password", Matchers.not(Matchers.equalTo(userDTO1.getPassword()))))
                .andExpect(jsonPath("password").value(passwordHashGenerator.hashPassword(userDTO1.getPassword())))
                .andExpect(jsonPath("$.role.id").value(1L))
                .andExpect(jsonPath("name").value("User1"));
    }

    @Test
    @Rollback
    public void testAddUser_shouldRespondWithUnauthorizedStatus() throws Exception {
        UserDTO userDTO1 = TestUtils.buildUserDTO(1);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO1)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Rollback
    public void testFindById_shouldRespondWithStatusCode200() throws Exception {
        UserDTO userDTO = TestUtils.buildUserDTO();
        User user = TestUtils.buildUser();
        userRepository.addUser(user);
        token = tokenManager.generateToken(userDTO);
        userRepository.addUser(TestUtils.buildUser(2));
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/users/2")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email").value("user2@email.com"))
                .andExpect(jsonPath("password").value("b982b70ab5106c703497b73af8b723e7cfbf00b87b9955ed05fb81a34f97df77"))
                .andExpect(jsonPath("$.role.id").value(2L))
                .andExpect(jsonPath("name").value("User2"));
    }

    @Test
    @Rollback
    public void testFindAll_shouldReturn3rdAnd4th() throws Exception {
        UserDTO userDTO = TestUtils.buildUserDTO();
        User user = TestUtils.buildUser();
        userRepository.addUser(user);
        token = tokenManager.generateToken(userDTO);
        for (int count = 1; count <= 5; count++) {
            userRepository.addUser(TestUtils.buildUser(count));
        }
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/users")
                        .param("page", "2")
                        .param("pageSize", "2")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3L))
                .andExpect(jsonPath("$[1].id").value(4L));
    }

    @Test
    @Rollback
    public void testFindAll_shouldReturnEmptyList() throws Exception {
        UserDTO userDTO = TestUtils.buildUserDTO();
        User user = TestUtils.buildUser();
        userRepository.addUser(user);
        token = tokenManager.generateToken(userDTO);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/users")
                        .param("page", "5")
                        .param("pageSize", "2")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @Rollback
    public void testAddUser_invalidField() throws Exception {
        UserDTO userDTO = TestUtils.buildUserDTO();
        User user = TestUtils.buildUser();
        userRepository.addUser(user);
        token = tokenManager.generateToken(userDTO);
        UserDTO userDTOInvalidPassword = TestUtils.buildUserDTOInvalidPassword();

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTOInvalidPassword))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @Rollback
    public void testDeleteUser() throws Exception {
        UserDTO userDTO = TestUtils.buildUserDTO();
        User user = TestUtils.buildUser();
        userRepository.addUser(user);
        token = tokenManager.generateToken(userDTO);
        userRepository.addUser(TestUtils.buildUser(1));

        mockMvc.perform(delete("/users/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    @Rollback
    public void testFindById_shouldThrowUserNotFound() throws Exception {
        UserDTO userDTO = TestUtils.buildUserDTO();
        User user = TestUtils.buildUser();
        userRepository.addUser(user);
        token = tokenManager.generateToken(userDTO);
        mockMvc.perform(MockMvcRequestBuilders.get("/users/100")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User with id: '100' was not found"));
    }

    @Test
    @Rollback
    public void testDeleteUser_shouldThrowUserNotFound() throws Exception {
        UserDTO userDTO = TestUtils.buildUserDTO();
        User user = TestUtils.buildUser();
        userRepository.addUser(user);
        token = tokenManager.generateToken(userDTO);
        mockMvc.perform(delete("/users/100")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User with id: '100' not found"));
    }

    @Test
    @Rollback
    void authorizationTest_deleteUser_shouldReceiveForbiddenStatusCode() throws Exception {
        userRepository.addUser(TestUtils.buildPTEUser());
        UserDTO userDTO = TestUtils.buildPTEUserDTO();
        String token = tokenManager.generateToken(userDTO);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    @Rollback
    void authorizationTest_addUser_shouldReceiveForbiddenStatusCode() throws Exception {
        UserDTO userDTO = TestUtils.buildPTEUserDTO();
        User user = TestUtils.buildPTEUser();
        userRepository.addUser(user);
        String token = tokenManager.generateToken(userDTO);

        UserDTO userDTO1 = TestUtils.buildUserDTO();

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO1))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    @Rollback
    public void testAreUsersValid_shouldRespondWithStatusCode200AndReturnTrue() throws Exception {
        User user1 = createAdmin();
        User user2 = createHrRepresentative();

        storeUsersInDB(user1, user2);

        UserEmailRoleDTO userEmailRoleDTO1 = mapUserToUserEmailRoleDTO(user1);
        UserEmailRoleDTO userEmailRoleDTO2 = mapUserToUserEmailRoleDTO(user2);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users/validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(userEmailRoleDTO1, userEmailRoleDTO2))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("valid").value("true"));
    }

    @Test
    @Rollback
    public void testAreUsersValid_whenOneDoesntExist_shouldRespondWithStatusCode200AndReturnFalse() throws Exception {
        User user1 = createAdmin();
        User user2 = createHrRepresentative();

        storeUsersInDB(user1);

        UserEmailRoleDTO userEmailRoleDTO1 = mapUserToUserEmailRoleDTO(user1);
        UserEmailRoleDTO userEmailRoleDTO2 = mapUserToUserEmailRoleDTO(user2);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users/validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(userEmailRoleDTO1, userEmailRoleDTO2))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("valid").value("false"));
    }

    @Test
    @Rollback
    public void testAreUsersValid_whenOneDoesntHaveGivenRole_shouldRespondWithStatusCode200AndReturnFalse() throws Exception {
        User user1 = createAdmin();
        User user2 = createHrRepresentative();

        storeUsersInDB(user1);

        UserEmailRoleDTO userEmailRoleDTO1 = mapUserToUserEmailRoleDTO(user1);
        UserEmailRoleDTO userEmailRoleDTO2 = mapUserToUserEmailRoleDTO(user2);
        userEmailRoleDTO2.setRoleName("BlaBlaRole");

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users/validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(userEmailRoleDTO1, userEmailRoleDTO2))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("valid").value("false"));
    }

    private User createAdmin() {
        User user = TestUtils.buildUser(1);
        user.setRole(TestUtils.buildAdminRole());
        return user;
    }

    private User createHrRepresentative() {
        User user = TestUtils.buildUser(2);
        user.setRole(TestUtils.buildHrRepresentativeRole());
        return user;
    }

    private void storeUsersInDB(User... users) {
        for (User user : users) {
            userRepository.addUser(user);
        }
    }

    private UserEmailRoleDTO mapUserToUserEmailRoleDTO(User user) {
        return new UserEmailRoleDTO(user.getEmail(), user.getRole().getName());
    }
}
