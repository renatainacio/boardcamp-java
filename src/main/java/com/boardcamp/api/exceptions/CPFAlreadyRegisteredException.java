package com.boardcamp.api.exceptions;

public class CPFAlreadyRegisteredException extends RuntimeException {
    public CPFAlreadyRegisteredException(){
        super("The informed CPF is already registered");
    }
}
