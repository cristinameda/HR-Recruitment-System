package com.nagarro.candidatemanagement.controller.dto;

import java.util.StringJoiner;

public class InterestedPositionDTO {
    private Long id;
    private String name;

    private InterestedPositionDTO() {

    }

    public InterestedPositionDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .toString();
    }
}
