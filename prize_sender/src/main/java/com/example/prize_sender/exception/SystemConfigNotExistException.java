package com.example.prize_sender.exception;

public class SystemConfigNotExistException extends RuntimeException {
    public SystemConfigNotExistException(String message) {
        super(message);
    }
}
