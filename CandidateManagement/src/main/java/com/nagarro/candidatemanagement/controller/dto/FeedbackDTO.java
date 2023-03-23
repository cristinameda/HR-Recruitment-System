package com.nagarro.candidatemanagement.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class FeedbackDTO {
    private String comment;
    private Long candidateId;
    private String userEmail;
    @NotNull
    private Boolean approved;
    @JsonFormat(pattern = "YYYY-MM-dd HH:mm:ss")
    private LocalDateTime date;

    public FeedbackDTO() {
    }

    public FeedbackDTO(String comment, Long candidateId, String userEmail, Boolean approved, LocalDateTime date) {
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

    public Long getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Long candidateId) {
        this.candidateId = candidateId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Boolean isApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FeedbackDTO that = (FeedbackDTO) o;

        if (!candidateId.equals(that.candidateId)) return false;
        return userEmail.equals(that.userEmail);
    }
}
