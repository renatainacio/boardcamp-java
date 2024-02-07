package com.boardcamp.api.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.boardcamp.api.dtos.GameDTO;
import com.boardcamp.api.exceptions.GameAlreadyExistsException;
import com.boardcamp.api.models.Game;
import com.boardcamp.api.repositories.GameRepository;

@Service
public class GameService {
    
    private final GameRepository gameRepository;

    GameService(GameRepository gameRepository){
        this.gameRepository = gameRepository;
    }

    public Game postGame(GameDTO gameDTO){

        boolean existingGame = !this.gameRepository.findByName(gameDTO.getName()).isEmpty();

        if (existingGame) 
            throw new GameAlreadyExistsException("Game already exists.");
        else {
            Game game = new Game(gameDTO);
    
            return this.gameRepository.save(game);
        }

    }

    public List<Game> getGames(){
        return this.gameRepository.findAll();
    }

}
