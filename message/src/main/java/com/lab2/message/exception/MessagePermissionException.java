package com.lab2.message.exception;

public class MessagePermissionException extends RuntimeException{

    public MessagePermissionException() {
        super();
    }

    public MessagePermissionException(String message) {
        super(message);
    }
}
