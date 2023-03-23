package com.nagarro.candidatemanagement.model;

import java.util.StringJoiner;
import javax.validation.constraints.NotBlank;
import org.springframework.data.annotation.Id;

public class InterestedPosition {
    @Id
    private Long id;
    @NotBlank
    private String name;

    public InterestedPosition() {

    }

    public InterestedPosition(Long id, String name) {
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
