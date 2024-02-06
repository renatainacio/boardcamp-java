package com.boardcamp.api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.boardcamp.api.dtos.GameDTO;
import com.boardcamp.api.models.Game;
import com.boardcamp.api.services.GameService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/games")
public class GameController {

    final GameService gameService;

    GameController(GameService gameService){
        this.gameService = gameService;
    }

    @PostMapping
    public ResponseEntity<Game> postGame(@RequestBody @Valid GameDTO gameDTO){
        Game game = gameService.postGame(gameDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(game);
    }
    
}
