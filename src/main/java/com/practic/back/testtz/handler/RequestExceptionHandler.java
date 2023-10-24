package com.practic.back.testtz.handler;


import com.practic.back.testtz.exception.NotFoundRequestException;
import com.practic.back.testtz.exception.RequestException;
import com.practic.back.testtz.exception.ValidRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class RequestExceptionHandler {

    @ExceptionHandler(value = ValidRequestException.class)
    public ResponseEntity<Object> handleException(ValidRequestException e) {
        final RequestException requestException =
                new RequestException(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return new ResponseEntity<>(requestException, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(value = NotFoundRequestException.class)
    public ResponseEntity<Object> handleException(NotFoundRequestException e) {
        final RequestException requestException =
                new RequestException(e.getMessage(), HttpStatus.NOT_FOUND, LocalDateTime.now());
        return new ResponseEntity<>(requestException, HttpStatus.NOT_FOUND);
    }

}
