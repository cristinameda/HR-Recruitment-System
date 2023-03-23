package com.nagarro.recruitmenthelper.usermanagement.domain.mapper;

import com.nagarro.recruitmenthelper.usermanagement.domain.Role;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RoleRowMapperTest {

    @Test
    void testMapRow() throws SQLException {
        Role role = new Role(1L, "Role");
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getLong("role_id")).thenReturn(1L);
        when(resultSet.getString("name")).thenReturn("Role");
        RoleRowMapper roleRowMapper = new RoleRowMapper();

        Role mappedRole = roleRowMapper.mapRow(resultSet, 1);

        assertNotNull(mappedRole);
        assertEquals(role.getId(), mappedRole.getId());
        assertEquals(role.getName(), mappedRole.getName());
    }
}