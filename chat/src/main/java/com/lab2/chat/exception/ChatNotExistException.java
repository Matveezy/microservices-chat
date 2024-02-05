package com.lab2.chat.exception;

public class ChatNotExistException extends RuntimeException{

    public ChatNotExistException() {
        super();
    }

    public ChatNotExistException(String message) {
        super(message);
    }
}
