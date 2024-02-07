package com.boardcamp.api.exceptions;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(){
        super("There is no customer with the informed id.");
    }
}
