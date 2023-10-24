package com.practic.back.testtz.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record RequestException(String massage, HttpStatus httpStatus, LocalDateTime dateTime) {
}
