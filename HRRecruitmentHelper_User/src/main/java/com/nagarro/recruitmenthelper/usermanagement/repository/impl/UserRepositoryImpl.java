package com.nagarro.recruitmenthelper.usermanagement.repository.impl;

import com.nagarro.recruitmenthelper.usermanagement.domain.User;
import com.nagarro.recruitmenthelper.usermanagement.domain.mapper.UserRowMapper;
import com.nagarro.recruitmenthelper.usermanagement.exception.UserHasNoRoleException;
import com.nagarro.recruitmenthelper.usermanagement.repository.UserRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserRepositoryImpl.class);
    private static final String INSERT_USER_STATEMENT = "INSERT INTO users(email, password, role_id, full_name) VALUES(?, ?, ?, ?)";
    private static final String SELECT_USER_AND_ROLE_BY_ID_QUERY = "SELECT * FROM users LEFT JOIN roles r ON r.role_id = users.role_id WHERE user_id=?";
    private static final String SELECT_PAGED_USERS_AND_ROLES_QUERY = "SELECT * FROM users LEFT JOIN roles r ON r.role_id = users.role_id ORDER BY user_id LIMIT ? OFFSET ?";
    private static final String DELETE_USER_STATEMENT = "DELETE FROM users WHERE user_id=?";
    private static final String FIND_USER_BY_EMAIL_QUERY = "SELECT * FROM users LEFT JOIN roles r ON r.role_id = users.role_id WHERE email = ?";
    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper userRowMapper;

    public UserRepositoryImpl(JdbcTemplate jdbcTemplate, UserRowMapper userRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRowMapper = userRowMapper;
    }

    @Override
    public User addUser(User user) {
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(con -> getPreparedStatement(user, con), keyHolder);
            Optional.ofNullable(keyHolder.getKey())
                    .ifPresent(key -> user.setId(key.longValue()));
            return user;
        } catch (DataAccessException e) {
            LOGGER.error("UserRepositoryImpl: addUser with email {} ", user.getEmail(), e);
            return null;
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(SELECT_USER_AND_ROLE_BY_ID_QUERY, userRowMapper, id));
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    @Override
    public List<User> findAll(int page, int pageSize) {
        long offset = (long) page * pageSize - pageSize;
        return jdbcTemplate.query(SELECT_PAGED_USERS_AND_ROLES_QUERY, userRowMapper, pageSize, offset);
    }

    @Override
    public void delete(long userId) {
        Object[] params = {userId};
        int affectedRows = jdbcTemplate.update(DELETE_USER_STATEMENT, params);
        LOGGER.info(" {} row(s) deleted.", affectedRows);
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(FIND_USER_BY_EMAIL_QUERY, userRowMapper, email));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private PreparedStatement getPreparedStatement(User user, Connection con) throws SQLException {
        PreparedStatement ps = con.prepareStatement(INSERT_USER_STATEMENT, new String[]{"user_id"});
        if (null == user.getRole()) {
            throw new UserHasNoRoleException("The given user with email '" + user.getEmail() + "' has no role!");
        }
        ps.setString(1, user.getEmail());
        ps.setString(2, user.getPassword());
        ps.setLong(3, user.getRole().getId());
        ps.setString(4, user.getName());
        return ps;
    }
}