package com.nagarro.recruitmenthelper.usermanagement.domain.mapper;

import com.nagarro.recruitmenthelper.usermanagement.domain.User;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class UserRowMapper implements RowMapper<User> {
    private final RoleRowMapper roleRowMapper;

    public UserRowMapper(RoleRowMapper roleRowMapper) {
        this.roleRowMapper = roleRowMapper;
    }

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("user_id"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setName(rs.getString("full_name"));
        user.setRole(roleRowMapper.mapRow(rs, rowNum));
        return user;
    }
}
