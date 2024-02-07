package com.boardcamp.api.exceptions;

public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException(){
        super("There is no game with the informed id.");
    }
}
