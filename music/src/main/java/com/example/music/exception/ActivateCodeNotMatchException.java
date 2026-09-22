package com.example.music.exception;

public class ActivateCodeNotMatchException extends RuntimeException {
    public ActivateCodeNotMatchException(String message) {
        super(message);
    }
}
