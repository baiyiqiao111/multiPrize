package com.example.prize_center.exception;

public class PrizeStockNotEnoughException extends RuntimeException {
    public PrizeStockNotEnoughException(String message) {
        super(message);
    }
}
