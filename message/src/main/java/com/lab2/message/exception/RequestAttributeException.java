package com.lab2.message.exception;

public class RequestAttributeException extends RuntimeException{

    public RequestAttributeException() {
    }

    public RequestAttributeException(String message) {
        super(message);
    }
}
