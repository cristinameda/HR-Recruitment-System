package com.nagarro.candidatemanagement.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import org.springframework.data.annotation.Id;

public class InterviewDTO {

    @Id
    private long id;
    @NotNull(message = "Candidate ID is mandatory!")
    @Positive
    private long candidateId;
    @NotNull(message = "Date is mandatory!")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Future
    private LocalDateTime dateTime;
    @NotBlank(message = "Location is mandatory!")
    private String location;

    public InterviewDTO() {
    }

    public InterviewDTO(long candidateId, LocalDateTime dateTime, String location) {
        this.candidateId = candidateId;
        this.dateTime = dateTime;
        this.location = location;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(long candidateId) {
        this.candidateId = candidateId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InterviewDTO that = (InterviewDTO) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, candidateId, dateTime, location);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("InterviewDTO{");
        sb.append("id=").append(id);
        sb.append(", candidateId=").append(candidateId);
        sb.append(", date=").append(dateTime);
        sb.append(", location='").append(location).append('\'');
        sb.append('}');
        return sb.toString();
    }
}

