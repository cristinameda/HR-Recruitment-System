package com.nagarro.recruitmenthelper.usermanagement.exception;

public class UserHasNoRoleException extends RuntimeException {
    public UserHasNoRoleException(String message) {
        super(message);
    }

}