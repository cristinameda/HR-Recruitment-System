package com.nagarro.candidatemanagement.model;

import java.time.LocalDateTime;

public class Feedback {
    private String comment;
    private long candidateId;
    private String userEmail;
    private boolean approved;
    private LocalDateTime date;

    public Feedback() {
    }

    public Feedback(String comment, long candidateId, String userEmail, boolean approved, LocalDateTime date) {
        this.comment = comment;
        this.candidateId = candidateId;
        this.userEmail = userEmail;
        this.approved = approved;
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(long candidateId) {
        this.candidateId = candidateId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
