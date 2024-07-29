package com.inghubs.exception;

public class StockAlreadyExistsException extends RuntimeException {
    public StockAlreadyExistsException(String message) {
        super(message);
    }
}
