package com.boardcamp.api.exceptions;

public class RentalNotFoundException extends RuntimeException {
    public RentalNotFoundException(){
        super("There is no rental with the informed id.");
    }
}
