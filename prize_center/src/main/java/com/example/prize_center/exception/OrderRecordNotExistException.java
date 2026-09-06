package com.example.prize_center.exception;

public class OrderRecordNotExistException extends RuntimeException {
    public OrderRecordNotExistException(String message) {
        super(message);
    }
}
