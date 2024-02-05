package com.lab2.message.exception;

public class ChatPermissionException extends RuntimeException{

    public ChatPermissionException() {
    }

    public ChatPermissionException(String message) {
        super(message);
    }
}
