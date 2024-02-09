package com.boardcamp.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.boardcamp.api.dtos.GameDTO;
import com.boardcamp.api.models.Game;
import com.boardcamp.api.repositories.GameRepository;
import com.boardcamp.api.repositories.RentalRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class GameIntegrationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private RentalRepository rentalRepository;

    @BeforeEach
    private void cleanupDatabase(){
        rentalRepository.deleteAll();
        gameRepository.deleteAll();
    }

    GameDTO createGame(){
		String name = "name";
		String image = "image";
		Long stockTotal = 4L;
		Long pricePerDay = 1000L;
		return new GameDTO(name, image, stockTotal, pricePerDay);
	}

    @Test
	void givenValidGame_whenCreatingGame_thenGameIsCreated(){
        //given
        GameDTO gameDTO = createGame();
        HttpEntity<GameDTO> body = new HttpEntity<>(gameDTO);

        //when
        ResponseEntity<Game> response = restTemplate.exchange(
            "/games", 
            HttpMethod.POST,
            body,
            Game.class
            );
        
        //then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(1, gameRepository.count());
        assertEquals(gameDTO.getName(), response.getBody().getName());
        assertEquals(gameDTO.getImage(), response.getBody().getImage());
        assertEquals(gameDTO.getPricePerDay(), response.getBody().getPricePerDay());
        assertEquals(gameDTO.getStockTotal(), response.getBody().getStockTotal());
    }

    @Test
	void givenNullName_whenCreatingGame_thenThrowError(){
        //given
        GameDTO gameDTO = new GameDTO(null, null, 3L, 1000L);
        HttpEntity<GameDTO> body = new HttpEntity<>(gameDTO);

        //when
        ResponseEntity<String> response = restTemplate.exchange(
            "/games", 
            HttpMethod.POST,
            body,
            String.class
            );
        
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(0, gameRepository.count());
        assertEquals("Name must not be blank", response.getBody());
    }

    @Test
	void givenBlankName_whenCreatingGame_thenThrowError(){
        //given
        GameDTO gameDTO = new GameDTO("", null, 3L, 1000L);
        HttpEntity<GameDTO> body = new HttpEntity<>(gameDTO);

        //when
        ResponseEntity<String> response = restTemplate.exchange(
            "/games", 
            HttpMethod.POST,
            body,
            String.class
            );
        
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(0, gameRepository.count());
        assertEquals("Name must not be blank", response.getBody());
    }

    @Test
	void givenNullStockTotal_whenCreatingGame_thenThrowError(){
        //given
        GameDTO gameDTO = new GameDTO("Monopoly", null, null, 1000L);
        HttpEntity<GameDTO> body = new HttpEntity<>(gameDTO);

        //when
        ResponseEntity<String> response = restTemplate.exchange(
            "/games", 
            HttpMethod.POST,
            body,
            String.class
            );
        
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(0, gameRepository.count());
        assertEquals("Stock Total must not be null", response.getBody());
    }

    @Test
	void givenZeroedStockTotal_whenCreatingGame_thenThrowError(){
        //given
        GameDTO gameDTO = new GameDTO("Monopoly", null, 0L, 1000L);
        HttpEntity<GameDTO> body = new HttpEntity<>(gameDTO);

        //when
        ResponseEntity<String> response = restTemplate.exchange(
            "/games", 
            HttpMethod.POST,
            body,
            String.class
            );
        
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(0, gameRepository.count());
        assertEquals("Stock Total should be greater than zero", response.getBody());
    }
    

    @Test
	void givenNegativeStockTotal_whenCreatingGame_thenThrowError(){
        //given
        GameDTO gameDTO = new GameDTO("Monopoly", null, -3L, 1000L);
        HttpEntity<GameDTO> body = new HttpEntity<>(gameDTO);

        //when
        ResponseEntity<String> response = restTemplate.exchange(
            "/games", 
            HttpMethod.POST,
            body,
            String.class
            );
        
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(0, gameRepository.count());
        assertEquals("Stock Total should be greater than zero", response.getBody());
    }
    
    @Test
	void givenNullPricePerDay_whenCreatingGame_thenThrowError(){
        //given
        GameDTO gameDTO = new GameDTO("Monopoly", null, 3L, null);
        HttpEntity<GameDTO> body = new HttpEntity<>(gameDTO);

        //when
        ResponseEntity<String> response = restTemplate.exchange(
            "/games", 
            HttpMethod.POST,
            body,
            String.class
            );
        
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(0, gameRepository.count());
        assertEquals("Price Per Day must not be null", response.getBody());
    }

    @Test
	void givenZeroedPricePerDay_whenCreatingGame_thenThrowError(){
        //given
        GameDTO gameDTO = new GameDTO("Monopoly", null, 3L, 0L);
        HttpEntity<GameDTO> body = new HttpEntity<>(gameDTO);

        //when
        ResponseEntity<String> response = restTemplate.exchange(
            "/games", 
            HttpMethod.POST,
            body,
            String.class
            );
        
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(0, gameRepository.count());
        assertEquals("Price Per Day should be greater than zero", response.getBody());
    }

    @Test
	void givenNegativePricePerDay_whenCreatingGame_thenThrowError(){
        //given
        GameDTO gameDTO = new GameDTO("Monopoly", null, 3L, -1000L);
        HttpEntity<GameDTO> body = new HttpEntity<>(gameDTO);

        //when
        ResponseEntity<String> response = restTemplate.exchange(
            "/games", 
            HttpMethod.POST,
            body,
            String.class
            );
        
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(0, gameRepository.count());
        assertEquals("Price Per Day should be greater than zero", response.getBody());
    }

    @Test
	void givenDuplicatedName_whenCreatingGame_thenThrowError(){
        //given
        GameDTO gameDTO = new GameDTO("Monopoly", null, 3L, 1000L);
        Game monopoly = new Game(gameDTO);
        gameRepository.save(monopoly);
        HttpEntity<GameDTO> body = new HttpEntity<>(gameDTO);

        //when
        ResponseEntity<String> response = restTemplate.exchange(
            "/games", 
            HttpMethod.POST,
            body,
            String.class
            );
        
        //then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(1, gameRepository.count());
        assertEquals("A game with this name already exists", response.getBody());
    }

    @Test
	void givenEmptyGameDatabase_whenGetGames_thenReturnEmptyArray(){
        //given

        //when
        ResponseEntity<ArrayList<Game>> response = restTemplate.exchange(
            "/games", 
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<ArrayList<Game>>() {}
            );
        
        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, gameRepository.count());
        assertEquals(0, response.getBody().size());
        assertEquals(new ArrayList<Game>(), response.getBody());
    }

    @Test
	void givenThreeGames_whenGetGames_thenReturnArray(){
        //given
        Game monopoly = new Game(new GameDTO("Monopoly", "", 1L, 1000L));
        Game chess = new Game(new GameDTO("Chess", "", 1L, 1000L));
        Game war = new Game(new GameDTO("War", "", 1L, 1000L));

        gameRepository.save(monopoly);
        gameRepository.save(chess);
        gameRepository.save(war);

        List<Game> list = new ArrayList<>();
        list.add(monopoly);
        list.add(chess);
        list.add(war);
        
        //when
        ResponseEntity<ArrayList<Game>> response = restTemplate.exchange(
            "/games", 
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<ArrayList<Game>>() {}
            );
        
        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(3, gameRepository.count());
        assertEquals(3, response.getBody().size());
        assertEquals(list, response.getBody());
    }
}
