package com.lab2.message.exception;

public class ChatNotFoundException extends RuntimeException{

    public ChatNotFoundException() {
        super();
    }

    public ChatNotFoundException(String message) {
        super(message);
    }
}
