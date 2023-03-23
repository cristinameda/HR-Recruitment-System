package com.nagarro.candidatemanagement.repository.impl;

import com.nagarro.candidatemanagement.mapper.CandidateRowMapper;
import com.nagarro.candidatemanagement.mapper.InterestedPositionRowMapper;
import com.nagarro.candidatemanagement.model.Candidate;
import com.nagarro.candidatemanagement.model.File;
import com.nagarro.candidatemanagement.model.InterestedPosition;
import com.nagarro.candidatemanagement.repository.CandidateRepository;
import com.nagarro.candidatemanagement.repository.exception.RepositoryException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class JdbcCandidateRepository implements CandidateRepository {
    private static final String INSERT_CANDIDATE_STATEMENT = "INSERT INTO candidates(name, phone_number, email," +
            " city, experience_years, faculty, recruitment_channel, birth_date) VALUES (?,?,?,?,?,?,?,?)";
    private static final String INSERT_CANDIDATE_POSITIONS_STATEMENT = "INSERT INTO candidates_positions(candidate_id,position_id) VALUES (?,?)";
    private static final String INSERT_CANDIDATE_USER_STATEMENT = "INSERT INTO candidates_users(candidate_id, user_email) VALUES (?, ?)";
    private static final String INSERT_CANDIDATE_FILES_STATEMENT = "INSERT INTO files(name,data,type,candidate_id) VALUES (?,?,?,?)";

    private static final String DELETE_CANDIDATE_USERS_STATEMENT = "DELETE FROM candidates_users WHERE candidate_id = ?";
    private static final String DELETE_CANDIDATE_STATEMENT = "DELETE FROM candidates WHERE candidate_id = ?";
    private static final String DELETE_CANDIDATE_POSITIONS_STATEMENT = "DELETE FROM candidates_positions WHERE candidate_id = ?";

    private static final String SELECT_ALL_CANDIDATES_QUERY = "SELECT * FROM candidates LIMIT ? OFFSET ?";
    private static final String SELECT_CANDIDATE_BY_ID_QUERY = "SELECT * FROM candidates WHERE candidate_id = ?";
    private static final String SELECT_POSITIONS_INDEX_QUERY = "SELECT position_id FROM candidates_positions WHERE candidate_id = ?";
    private static final String SELECT_POSITIONS_QUERY = "SELECT * FROM positions WHERE position_id = ?";
    private static final String SELECT_CANDIDATE_BY_EMAIL_QUERY = "SELECT * FROM candidates WHERE email = ?";
    private static final String SELECT_CANDIDATES_BY_FIELD_QUERY = "SELECT * FROM candidates WHERE %s ILIKE ? LIMIT ? OFFSET ?";
    private static final String SELECT_CANDIDATES_BY_ASSIGNED_USER_QUERY = "SELECT * FROM candidates c LEFT JOIN candidates_users cu ON c.candidate_id = cu.candidate_id WHERE user_email = ? LIMIT ? OFFSET ?";
    private static final String SELECT_CANDIDATE_USERS_QUERY = "SELECT user_email FROM candidates_users WHERE candidate_id=?";

    private static final String UPDATE_CANDIDATE_STATUS_STATEMENT = "UPDATE candidates SET status = ? WHERE email = ?";

    private static final Set<String> VALID_FIELDS_TO_FILTER_BY = Set.of("email", "name");

    private final JdbcTemplate jdbcTemplate;

    public JdbcCandidateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Candidate save(Candidate candidate) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> getCandidatePreparedStatement(connection, candidate), keyHolder);
        candidate.setId(getKey(keyHolder, "candidate_id"));
        candidate.setStatus(findById(candidate.getId()).get().getStatus());
        insertCandidatePositions(candidate);
        candidate.getFiles().forEach(file -> {
            jdbcTemplate.update(connection -> getPreparedStatementFile(connection, file, candidate.getId()), keyHolder);
            file.setId(getKey(keyHolder, "file_id"));
        });
        return candidate;
    }

    @Override
    public List<Candidate> findAll(int pageNo, int pageSize) {
        long offset = (long) pageNo * pageSize - pageSize;
        List<Candidate> allCandidates = jdbcTemplate.query(SELECT_ALL_CANDIDATES_QUERY, new CandidateRowMapper(), pageSize, offset);
        attachRelatedObjects(allCandidates);
        return allCandidates;
    }

    @Override
    public List<Candidate> findAllByField(int pageNo, int pageSize, String field, String filterValue) {
        if (!VALID_FIELDS_TO_FILTER_BY.contains(field)) {
            throw new RepositoryException("Cannot filter candidates by field '" + field + "'!");
        }
        long offset = (long) pageNo * pageSize - pageSize;
        filterValue = '%' + filterValue + '%';
        String completeSelectStatementByField = String.format(SELECT_CANDIDATES_BY_FIELD_QUERY, field);
        return getCandidates(pageSize, offset, filterValue, completeSelectStatementByField);
    }

    @Override
    public List<Candidate> findAllByAssignedUser(Integer pageNo, Integer pageSize, String userEmail) {
        long offset = (long) pageNo * pageSize - pageSize;
        return getCandidates(pageSize, offset, userEmail, SELECT_CANDIDATES_BY_ASSIGNED_USER_QUERY);
    }

    @Override
    public Optional<Candidate> findById(Long candidateId) {
        Candidate candidate;
        try {
            candidate = jdbcTemplate.queryForObject(SELECT_CANDIDATE_BY_ID_QUERY, new CandidateRowMapper(), candidateId);
            attachRelatedObjects(candidate);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
        return Optional.of(candidate);
    }

    @Override
    public Optional<Candidate> findByEmail(String email) {
        Candidate candidate;
        try {
            candidate = jdbcTemplate.queryForObject(SELECT_CANDIDATE_BY_EMAIL_QUERY, new CandidateRowMapper(), email);
            attachRelatedObjects(candidate);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
        return Optional.of(candidate);
    }

    @Override
    @Transactional
    public boolean delete(long id) {
        jdbcTemplate.update(DELETE_CANDIDATE_POSITIONS_STATEMENT, id);
        deleteCandidateAssignedUsers(id);
        int returnedRowsCandidate = jdbcTemplate.update(DELETE_CANDIDATE_STATEMENT, id);
        return returnedRowsCandidate != 0;
    }

    @Override
    @Transactional
    public void updateCandidateAssignedUsers(Candidate candidate) {
        deleteCandidateAssignedUsers(candidate.getId());
        List<String> assignedUsers = candidate.getAssignedUsers();
        if (assignedUsers != null) {
            assignedUsers.forEach((user -> jdbcTemplate.update(INSERT_CANDIDATE_USER_STATEMENT, candidate.getId(), user)));
        }
    }

    private PreparedStatement getPreparedStatementFile(Connection connection, File file, long idCandidate) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_CANDIDATE_FILES_STATEMENT, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1, file.getName());
        preparedStatement.setBytes(2, file.getData());
        preparedStatement.setString(3, file.getType().toString());
        preparedStatement.setLong(4, idCandidate);
        return preparedStatement;
    }

    @Override
    @Transactional
    public void updateCandidateStatus(Candidate candidate) {
        jdbcTemplate.update(UPDATE_CANDIDATE_STATUS_STATEMENT, candidate.getStatus().toString(), candidate.getEmail());
    }

    private PreparedStatement getCandidatePreparedStatement(Connection connection, Candidate candidate) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_CANDIDATE_STATEMENT, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1, candidate.getName());
        preparedStatement.setString(2, candidate.getPhoneNumber());
        preparedStatement.setString(3, candidate.getEmail());
        preparedStatement.setString(4, candidate.getCity());
        preparedStatement.setInt(5, candidate.getExperienceYears());
        preparedStatement.setString(6, candidate.getFaculty());
        preparedStatement.setString(7, candidate.getRecruitmentChannel());
        preparedStatement.setString(8, candidate.getBirthDate().toString());
        return preparedStatement;
    }

    private List<Candidate> getCandidates(int pageSize, long offset, String filterValue, String selectStatementByField) {
        List<Candidate> candidates = jdbcTemplate.query(selectStatementByField, new CandidateRowMapper(), filterValue, pageSize, offset);
        attachRelatedObjects(candidates);
        return candidates;
    }

    private void attachRelatedObjects(List<Candidate> candidates) {
        for (Candidate candidate : candidates) {
            attachRelatedObjects(candidate);
        }
    }

    private void attachRelatedObjects(Candidate candidate) {
        List<Long> positionsIndex = getPositionsIndex(candidate);
        candidate.setInterestedPositions(getInterestedPosition(positionsIndex));
        candidate.setAssignedUsers(getAssignedUsers(candidate));
    }

    private void insertCandidatePositions(Candidate candidate) {
        List<InterestedPosition> interestedPositionsList = candidate.getInterestedPositions();
        if (interestedPositionsList != null) {
            interestedPositionsList.forEach(positionId -> jdbcTemplate.update(INSERT_CANDIDATE_POSITIONS_STATEMENT, candidate.getId(), positionId.getId()));
        }
    }

    private List<Long> getPositionsIndex(Candidate candidate) {
        List<String> data = jdbcTemplate.query(SELECT_POSITIONS_INDEX_QUERY, (rs, rowNum) -> rs.getString(1), candidate.getId());
        return data.stream().map(Long::parseLong).collect(Collectors.toList());
    }

    private List<InterestedPosition> getInterestedPosition(List<Long> positionsIdList) {
        List<InterestedPosition> interestedPositions = new ArrayList<>();
        for (Long index : positionsIdList) {
            interestedPositions.addAll(jdbcTemplate.query(SELECT_POSITIONS_QUERY, new InterestedPositionRowMapper(), index));
        }
        return interestedPositions;
    }

    private long getKey(KeyHolder keyHolder, String id) {
        return (long) Optional.ofNullable(keyHolder.getKeys())
                .orElseThrow(() -> new RepositoryException("Error retrieving key!"))
                .get(id);
    }

    private List<String> getAssignedUsers(Candidate candidate) {
        return jdbcTemplate.query(SELECT_CANDIDATE_USERS_QUERY, (rs, rowNum) -> rs.getString("user_email"), candidate.getId());
    }

    private void deleteCandidateAssignedUsers(Long id) {
        jdbcTemplate.update(DELETE_CANDIDATE_USERS_STATEMENT, id);
    }
}
