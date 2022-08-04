package com.tweetapp.auth.exception;

public class InvalidUserCredentialsException extends Exception{

    public InvalidUserCredentialsException(String message) {
        super(message);
    }
}
