package com.boardcamp.api.exceptions;

public class CPFAlreadyRegisteredException extends RuntimeException {
    public CPFAlreadyRegisteredException(String message){
        super(message);
    }
}
