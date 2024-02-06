package com.boardcamp.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ GameAlreadyExistsException.class })
    public ResponseEntity<String> handleGameAlreadyExists(GameAlreadyExistsException exception){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
    }

    @ExceptionHandler({ MethodArgumentNotValidException.class })
    public ResponseEntity<String> handleMethodArgumentNotValid(MethodArgumentNotValidException exception){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getFieldError().getDefaultMessage());
    }

}
