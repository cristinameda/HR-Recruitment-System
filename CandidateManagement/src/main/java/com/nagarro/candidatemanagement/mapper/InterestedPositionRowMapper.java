package com.nagarro.candidatemanagement.mapper;

import com.nagarro.candidatemanagement.model.InterestedPosition;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class InterestedPositionRowMapper implements RowMapper<InterestedPosition> {

    @Override
    public InterestedPosition mapRow(ResultSet rs, int rowNum) throws SQLException {
        InterestedPosition interestedPosition = new InterestedPosition();
        interestedPosition.setId(rs.getLong("position_id"));
        interestedPosition.setName(rs.getString("name"));
        return interestedPosition;
    }
}
