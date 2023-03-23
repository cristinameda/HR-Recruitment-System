package com.nagarro.recruitmenthelper.usermanagement.domain.mapper;

import com.nagarro.recruitmenthelper.usermanagement.domain.Role;
import com.nagarro.recruitmenthelper.usermanagement.domain.User;
import com.nagarro.recruitmenthelper.usermanagement.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserRowMapperTest {

    @Autowired
    private UserRowMapper userRowMapper;

    @Test
    public void testMapRow() throws SQLException {
        Role role = new Role(1L, "Role");
        User user = new User("Full Name", "email", "password", role);
        user.setId(1L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getLong("user_id")).thenReturn(1L);
        when(resultSet.getString("email")).thenReturn("email");
        when(resultSet.getString("password")).thenReturn("password");
        when(resultSet.getLong("role_id")).thenReturn(1L);
        when(resultSet.getString("name")).thenReturn("Role");
        when(resultSet.getString("full_name")).thenReturn("Full Name");

        User mappedUser = userRowMapper.mapRow(resultSet, 1);

        assertNotNull(mappedUser);
        TestUtils.equals(user, mappedUser);
    }
}