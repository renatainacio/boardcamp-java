package com.boardcamp.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.boardcamp.api.dtos.GameDTO;
import com.boardcamp.api.exceptions.GameAlreadyExistsException;
import com.boardcamp.api.models.Game;
import com.boardcamp.api.repositories.GameRepository;
import com.boardcamp.api.services.GameService;

@SpringBootTest
class GameUnitTests {

	@InjectMocks
	private GameService gameService;

	@Mock
	private GameRepository gameRepository;

	GameDTO createGame(){
		String name = "name";
		String image = "image";
		Long stockTotal = 4L;
		Long pricePerDay = 1000L;
		return new GameDTO(name, image, stockTotal, pricePerDay);
	}

	@Test
	void givenValidGame_whenCreatingGame_thenGameIsCreated(){

		GameDTO gameDTO = createGame();
		Game newGame = new Game(gameDTO);

		doReturn(false).when(gameRepository).existsByName(any());
		doReturn(newGame).when(gameRepository).save(newGame);

		//when
		Game game = gameService.postGame(gameDTO);

		//then

		verify(gameRepository, times(1)).existsByName("name");
		verify(gameRepository, times(1)).save(any());
		assertNotNull(game);
		assertEquals(newGame, game);
	}

	@Test
	void givenRepeatedGame_whenCreatingGame_thenThrowError(){
		//given

		GameDTO gameDTO = createGame();

		doReturn(true).when(gameRepository).existsByName(any());

		//when
		GameAlreadyExistsException exception = assertThrows(
			GameAlreadyExistsException.class,
			() -> gameService.postGame(gameDTO));

		//then
		assertNotNull(exception);
		assertEquals("A game with this name already exists", exception.getMessage());
		verify(gameRepository, times(1)).existsByName("name");
		verify(gameRepository, times(0)).save(any());

	}

}
