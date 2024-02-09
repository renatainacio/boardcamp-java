package com.boardcamp.api.exceptions;

public class RentalAlreadyFinishedException extends RuntimeException {
    public RentalAlreadyFinishedException(){
        super("This rental is already finished");
    }
}
