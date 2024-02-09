package com.boardcamp.api.exceptions;

public class NoUnitsAvailableException extends RuntimeException {
    public NoUnitsAvailableException(){
        super("All units of this game are already rented");
    }
}
