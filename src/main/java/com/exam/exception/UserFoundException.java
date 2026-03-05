package com.exam.exception;

public class UserFoundException extends RuntimeException {

    public UserFoundException() {
        super("User with this username already exists.");
    }

    public UserFoundException(String message) {
        super(message);
    }
}
