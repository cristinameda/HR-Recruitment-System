package com.nagarro.candidatemanagement.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Interview {
    private long id;
    private long candidateId;
    private LocalDateTime dateTime;
    private String location;

    public Interview() {
    }

    public Interview(long candidateId, LocalDateTime dateTime, String location) {
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
        Interview interview = (Interview) o;
        return id == interview.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, candidateId, dateTime, location);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Interview{");
        sb.append("id=").append(id);
        sb.append(", candidateId=").append(candidateId);
        sb.append(", date=").append(dateTime);
        sb.append(", location='").append(location).append('\'');
        sb.append('}');
        return sb.toString();
    }
}

