package com.nagarro.recruitmenthelper.usermanagement.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;


@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(value = {UserNotFoundException.class})
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException e) {
        handleLogging(e, HttpStatus.NOT_FOUND);
        ApiException apiException = new ApiException(HttpStatus.NOT_FOUND, e.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(apiException, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {UserHasNoRoleException.class, IllegalArgumentException.class})
    public ResponseEntity<Object> handleBadRequestExceptions(RuntimeException e) {
        handleLogging(e, HttpStatus.BAD_REQUEST);
        ApiException apiException = new ApiException(HttpStatus.BAD_REQUEST, e.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(apiException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {HeaderAuthorizationException.class, InvalidCredentialsException.class, InvalidTokenException.class})
    public ResponseEntity<Object> handleHeaderAuthorizationException(RuntimeException e) {
        handleLogging(e, HttpStatus.UNAUTHORIZED);
        ApiException apiException = new ApiException(HttpStatus.UNAUTHORIZED, e.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(apiException, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {AuthorizationException.class})
    public ResponseEntity<Object> handleHeaderAuthorizationException(AuthorizationException e) {
        handleLogging(e, HttpStatus.FORBIDDEN);
        ApiException apiException = new ApiException(HttpStatus.FORBIDDEN, e.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(apiException, HttpStatus.FORBIDDEN);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ApiException apiException = new ApiException(HttpStatus.BAD_REQUEST);
        StringBuilder stringBuilder = new StringBuilder();
        handleLogging(e, HttpStatus.BAD_REQUEST);
        e.getBindingResult().getAllErrors()
                .stream()
                .filter(FieldError.class::isInstance)
                .map(FieldError.class::cast).forEach((error) -> insertErrorMessages(apiException, error, stringBuilder));
        apiException.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<>(apiException, HttpStatus.BAD_REQUEST);
    }

    private void insertErrorMessages(ApiException apiException, FieldError errorField, StringBuilder errors) {
        String errorMessage = errorField.getDefaultMessage();
        errors.append(errorMessage).append(" ");
        apiException.setMessage(errors.toString());
    }

    private void handleLogging(Throwable e, HttpStatus status) {
        if (status.is5xxServerError()) {
            LOGGER.error("A {} server error occurred", status, e);
        } else if (status.is4xxClientError()) {
            LOGGER.warn("A {} client error occurred", status, e);
        } else {
            LOGGER.debug("A {} error occurred", status, e);
        }
    }
}