package com.tweetapp.auth.exception;

import com.tweetapp.auth.model.ApiExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiExceptionResponse> handleUserNotFoundException(UserNotFoundException e) {
        HttpStatus notFound = HttpStatus.NOT_FOUND;
        ApiExceptionResponse apiExceptionResponse =
                new ApiExceptionResponse(e.getMessage(), notFound.value(), notFound, LocalDateTime.now());
        return new ResponseEntity<>(apiExceptionResponse, notFound);
    }

    @ExceptionHandler(InvalidUserCredentialsException.class)
    public ResponseEntity<ApiExceptionResponse> handleInvalidUserCredentialsException(InvalidUserCredentialsException e) {
        HttpStatus notFound = HttpStatus.NOT_FOUND;
        ApiExceptionResponse apiExceptionResponse =
                new ApiExceptionResponse(e.getMessage(), notFound.value(), notFound, LocalDateTime.now());
        return new ResponseEntity<>(apiExceptionResponse, notFound);
    }
}
