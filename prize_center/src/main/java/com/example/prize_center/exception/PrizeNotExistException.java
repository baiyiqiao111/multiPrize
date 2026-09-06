package com.example.prize_center.exception;

public class PrizeNotExistException extends RuntimeException {
    public PrizeNotExistException(String message) {
        super(message);
    }
}
