package com.nagarro.candidatemanagement.controller.dto;

import com.nagarro.candidatemanagement.model.CandidateStatus;

public class UpdateCandidateStatusDTO {
    private String email;
    private CandidateStatus status;

    public UpdateCandidateStatusDTO() {
    }

    public UpdateCandidateStatusDTO(String email, CandidateStatus status) {
        this.email = email;
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public CandidateStatus getStatus() {
        return status;
    }

    public void setStatus(CandidateStatus status) {
        this.status = status;
    }
}
