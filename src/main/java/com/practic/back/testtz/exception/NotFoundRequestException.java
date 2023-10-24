package com.practic.back.testtz.exception;

public class NotFoundRequestException extends RuntimeException{
    public NotFoundRequestException(String message) {
        super(message);
    }
}
