package com.nagarro.candidatemanagement.mapper;

import com.nagarro.candidatemanagement.model.InterestedPosition;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InterestedPositionRowMapperTest {
    @Test
    void testMapRow() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        InterestedPosition interestedPosition = new InterestedPosition(1L, "Back-end developer");
        when(resultSet.getLong("position_id")).thenReturn(1L);
        when(resultSet.getString("name")).thenReturn("Back-end developer");
        InterestedPositionRowMapper interestedPositionRowMapper = new InterestedPositionRowMapper();

        InterestedPosition mappedInterestedPosition = interestedPositionRowMapper.mapRow(resultSet, 1);

        assertEquals(interestedPosition.getId(), mappedInterestedPosition.getId());
        assertEquals(interestedPosition.getName(), mappedInterestedPosition.getName());
    }

}