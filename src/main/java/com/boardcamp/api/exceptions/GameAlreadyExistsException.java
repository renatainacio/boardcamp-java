package com.boardcamp.api.exceptions;

public class GameAlreadyExistsException extends RuntimeException {
    public GameAlreadyExistsException(){
        super("A game with this name already exists");
    }
}
