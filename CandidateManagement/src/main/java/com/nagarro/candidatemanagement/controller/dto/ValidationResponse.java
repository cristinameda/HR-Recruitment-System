package com.nagarro.candidatemanagement.controller.dto;

public class ValidationResponse {
    private Boolean valid;

    public ValidationResponse() {
    }

    public ValidationResponse(Boolean valid) {
        this.valid = valid;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }
}
