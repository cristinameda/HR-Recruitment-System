package com.nagarro.recruitmenthelper.usermanagement.repository.impl;

import com.nagarro.recruitmenthelper.usermanagement.domain.Role;
import com.nagarro.recruitmenthelper.usermanagement.domain.User;
import com.nagarro.recruitmenthelper.usermanagement.exception.UserHasNoRoleException;
import com.nagarro.recruitmenthelper.usermanagement.repository.UserRepository;
import com.nagarro.recruitmenthelper.usermanagement.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Sql({"classpath:scripts/droptable.sql", "classpath:scripts/createtable.sql", "classpath:scripts/insertroles.sql", "classpath:scripts/insertusers.sql"})
@ActiveProfiles("test")
public class ITUserRepository {

    private static final Role ADMIN_ROLE = new Role(1L, "Admin");
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private UserRepository userRepository;

    @Test
    @Rollback
    public void testAddUser() {
        var initialNr = JdbcTestUtils.countRowsInTable(jdbcTemplate, "users");
        User user = TestUtils.buildUser();
        user.setId(1L);

        var storedUser = userRepository.addUser(user);

        TestUtils.equals(user, storedUser);
        var actualNr = JdbcTestUtils.countRowsInTable(jdbcTemplate, "users");
        assertEquals(initialNr + 1, actualNr);
    }

    @Test
    @Rollback
    public void testAddUser_shouldThrowUserHasNoRoleException() {
        assertThrows(UserHasNoRoleException.class, () -> {
            User user = new User("User 1", "user1@yahoo.com", "password", null);
            userRepository.addUser(user);
        });
    }

    @Test
    @Rollback
    public void testFindUserById_shouldReturnUserInOptional() {
        User user = new User("John Doe", "test@yahoo.com", "123", ADMIN_ROLE);
        user.setId(1L);
        Optional<User> userOptional = userRepository.findById(1L);

        assertTrue(userOptional.isPresent());
        TestUtils.equals(user, userOptional.get());
    }

    @Test
    @Rollback
    public void testFindUserById_shouldReturnEmptyOptional() {
        Optional<User> userOptional = userRepository.findById(100L);

        assertTrue(userOptional.isEmpty());
    }

    @Test
    @Rollback
    public void testFindAll_shouldReturnAllUsers() {
        List<User> usersFirstPage = userRepository.findAll(1, 4);

        assertEquals(4, usersFirstPage.size());
        assertEquals(1L, usersFirstPage.get(0).getId());
        assertEquals(4L, usersFirstPage.get(3).getId());
    }

    @Test
    @Rollback
    public void testFindAll_shouldReturn3rdAnd4th() {
        List<User> usersSecondPage = userRepository.findAll(2, 2);

        assertEquals(2, usersSecondPage.size());
        assertEquals(3L, usersSecondPage.get(0).getId());
        assertEquals(4L, usersSecondPage.get(1).getId());
    }

    @Test
    @Rollback
    public void testDeleteUser() {
        long userId = 1L;
        var initialNr = JdbcTestUtils.countRowsInTable(jdbcTemplate, "users");

        userRepository.delete(userId);

        var actualNr = JdbcTestUtils.countRowsInTable(jdbcTemplate, "users");
        assertEquals(initialNr - 1, actualNr);
    }

    @Test
    @Rollback
    public void testDeleteUser_NotFound() {
        var initialNr = JdbcTestUtils.countRowsInTable(jdbcTemplate, "users");

        var actualNr = JdbcTestUtils.countRowsInTable(jdbcTemplate, "users");
        assertEquals(initialNr, actualNr);
    }

    @Test
    @Rollback
    public void testFindUserByEmail() {
        User expectedUser = new User("John Doe", "test@yahoo.com", "123", ADMIN_ROLE);
        expectedUser.setId(1L);
        String email = "test@yahoo.com";

        Optional<User> actualUser = userRepository.findUserByEmail(email);

        assertTrue(actualUser.isPresent());
        TestUtils.equals(expectedUser, actualUser.get());
    }

    @Test
    @Rollback
    public void testFindUserByEmail_userNotFound() {
        String email = "nonExistingEmail@yahoo.com";

        Optional<User> actualUser = userRepository.findUserByEmail(email);

        assertTrue(actualUser.isEmpty());
    }
}