package com.boardcamp.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.boardcamp.api.dtos.CustomerDTO;
import com.boardcamp.api.dtos.GameDTO;
import com.boardcamp.api.dtos.RentalDTO;
import com.boardcamp.api.models.Customer;
import com.boardcamp.api.models.Game;
import com.boardcamp.api.models.Rental;
import com.boardcamp.api.repositories.CustomerRepository;
import com.boardcamp.api.repositories.GameRepository;
import com.boardcamp.api.repositories.RentalRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class RentalIntegrationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RentalRepository rentalRepository;

    @BeforeEach
    private void cleanupDatabase(){
        rentalRepository.deleteAll();
        gameRepository.deleteAll();
        customerRepository.deleteAll();
    }

    GameDTO createGame(){
		String name = "name";
		String image = "image";
		Long stockTotal = 4L;
		Long pricePerDay = 1000L;
		return new GameDTO(name, image, stockTotal, pricePerDay);
	}

    CustomerDTO createCustomer(){
		String name = "John Doe";
		String cpf = "xxxxxxxxxxx";
		return new CustomerDTO(name, cpf);
	}

    @Test
	void givenValidRentalData_whenCreatingRental_thenRentalIsCreated(){
        //given
        Customer customer = new Customer(createCustomer());
        customerRepository.save(customer);
        Game game = new Game(createGame());
        gameRepository.save(game);
        LocalDate today = LocalDate.now();

        RentalDTO rentalDTO = new RentalDTO(customer.getId(), game.getId(), 2L);

        HttpEntity<RentalDTO> body = new HttpEntity<>(rentalDTO);

        //when
        ResponseEntity<Rental> response = restTemplate.exchange(
            "/rentals", 
            HttpMethod.POST,
            body,
            Rental.class
            );
        
        //then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(1, rentalRepository.count());
        assertEquals(rentalDTO.getGameId(), response.getBody().getGame().getId());
        assertEquals(rentalDTO.getCustomerId(), response.getBody().getCustomer().getId());
        assertEquals(rentalDTO.getDaysRented(), response.getBody().getDaysRented());
        assertEquals(0, response.getBody().getDelayFee());
        assertEquals(rentalDTO.getDaysRented() * game.getPricePerDay(), response.getBody().getOriginalPrice());
        assertEquals(today, response.getBody().getRentDate());
        assertEquals(null, response.getBody().getReturnDate());
    }

    @Test
	void givenNullDaysRented_whenCreatingRental_throwError(){
        //given
        Customer customer = new Customer(createCustomer());
        customerRepository.save(customer);
        Game game = new Game(createGame());
        gameRepository.save(game);

        RentalDTO rentalDTO = new RentalDTO(customer.getId(), game.getId(), null);

        HttpEntity<RentalDTO> body = new HttpEntity<>(rentalDTO);

        //when
        ResponseEntity<String> response = restTemplate.exchange(
            "/rentals", 
            HttpMethod.POST,
            body,
            String.class
            );
        
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(0, rentalRepository.count());
        assertEquals("Days Rented must not be null", response.getBody());
    }

    @Test
	void givenZeroedDaysRented_whenCreatingRental_throwError(){
        //given
        Customer customer = new Customer(createCustomer());
        customerRepository.save(customer);
        Game game = new Game(createGame());
        gameRepository.save(game);

        RentalDTO rentalDTO = new RentalDTO(customer.getId(), game.getId(), 0L);

        HttpEntity<RentalDTO> body = new HttpEntity<>(rentalDTO);

        //when
        ResponseEntity<String> response = restTemplate.exchange(
            "/rentals", 
            HttpMethod.POST,
            body,
            String.class
            );
        
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(0, rentalRepository.count());
        assertEquals("Days Rented should be greater than zero", response.getBody());
    }

    @Test
	void givenNegativeDaysRented_whenCreatingRental_throwError(){
        //given
        Customer customer = new Customer(createCustomer());
        customerRepository.save(customer);
        Game game = new Game(createGame());
        gameRepository.save(game);

        RentalDTO rentalDTO = new RentalDTO(customer.getId(), game.getId(), -2L);

        HttpEntity<RentalDTO> body = new HttpEntity<>(rentalDTO);

        //when
        ResponseEntity<String> response = restTemplate.exchange(
            "/rentals", 
            HttpMethod.POST,
            body,
            String.class
            );
        
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(0, rentalRepository.count());
        assertEquals("Days Rented should be greater than zero", response.getBody());
    }

    @Test
	void givenNullGameId_whenCreatingRental_throwError(){
        //given
        Customer customer = new Customer(createCustomer());
        customerRepository.save(customer);

        RentalDTO rentalDTO = new RentalDTO(customer.getId(), null, 2L);

        HttpEntity<RentalDTO> body = new HttpEntity<>(rentalDTO);

        //when
        ResponseEntity<String> response = restTemplate.exchange(
            "/rentals", 
            HttpMethod.POST,
            body,
            String.class
            );
        
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(0, rentalRepository.count());
        assertEquals("Game Id must not be null", response.getBody());
    }

    @Test
	void givenZeroedGameId_whenCreatingRental_throwError(){
        //given
        Customer customer = new Customer(createCustomer());
        customerRepository.save(customer);

        RentalDTO rentalDTO = new RentalDTO(customer.getId(), 0L, 2L);

        HttpEntity<RentalDTO> body = new HttpEntity<>(rentalDTO);

        //when
        ResponseEntity<String> response = restTemplate.exchange(
            "/rentals", 
            HttpMethod.POST,
            body,
            String.class
            );
        
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(0, rentalRepository.count());
        assertEquals("Game Id should be greater than zero", response.getBody());
    }

    @Test
	void givenNegativeGameId_whenCreatingRental_throwError(){
        //given
        Customer customer = new Customer(createCustomer());
        customerRepository.save(customer);

        RentalDTO rentalDTO = new RentalDTO(customer.getId(), -2L, 2L);

        HttpEntity<RentalDTO> body = new HttpEntity<>(rentalDTO);

        //when
        ResponseEntity<String> response = restTemplate.exchange(
            "/rentals", 
            HttpMethod.POST,
            body,
            String.class
            );
        
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(0, rentalRepository.count());
        assertEquals("Game Id should be greater than zero", response.getBody());
    }

    @Test
	void givenNullCustomerId_whenCreatingRental_throwError(){
        //given
        Game game = new Game(createGame());
        gameRepository.save(game);

        RentalDTO rentalDTO = new RentalDTO(null, game.getId(), 2L);

        HttpEntity<RentalDTO> body = new HttpEntity<>(rentalDTO);

        //when
        ResponseEntity<String> response = restTemplate.exchange(
            "/rentals", 
            HttpMethod.POST,
            body,
            String.class
            );
        
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(0, rentalRepository.count());
        assertEquals("Customer Id must not be null", response.getBody());
    }

    @Test
	void givenZeroedCustomerId_whenCreatingRental_throwError(){
        //given
        Game game = new Game(createGame());
        gameRepository.save(game);

        RentalDTO rentalDTO = new RentalDTO(0L, game.getId(), 2L);

        HttpEntity<RentalDTO> body = new HttpEntity<>(rentalDTO);

        //when
        ResponseEntity<String> response = restTemplate.exchange(
            "/rentals", 
            HttpMethod.POST,
            body,
            String.class
            );
        
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(0, rentalRepository.count());
        assertEquals("Customer Id should be greater than zero", response.getBody());
    }

    @Test
	void givenNegativeCustomerId_whenCreatingRental_throwError(){
        //given
        Game game = new Game(createGame());
        gameRepository.save(game);

        RentalDTO rentalDTO = new RentalDTO(-2L, game.getId(), 2L);

        HttpEntity<RentalDTO> body = new HttpEntity<>(rentalDTO);

        //when
        ResponseEntity<String> response = restTemplate.exchange(
            "/rentals", 
            HttpMethod.POST,
            body,
            String.class
            );
        
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(0, rentalRepository.count());
        assertEquals("Customer Id should be greater than zero", response.getBody());
    }

    @Test
	void givenNonExistingGameId_whenCreatingRental_throwError(){
        //given
        Customer customer = new Customer(createCustomer());
        customerRepository.save(customer);
        Game game = new Game(createGame());
        gameRepository.save(game);
        gameRepository.delete(game);

        RentalDTO rentalDTO = new RentalDTO(customer.getId(), game.getId(), 2L);

        HttpEntity<RentalDTO> body = new HttpEntity<>(rentalDTO);

        //when
        ResponseEntity<String> response = restTemplate.exchange(
            "/rentals", 
            HttpMethod.POST,
            body,
            String.class
            );
        
        //then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(0, rentalRepository.count());
        assertEquals("There is no game with the informed id", response.getBody());
    }

    @Test
	void givenNonExistingCustomerId_whenCreatingRental_throwError(){
        //given
        Customer customer = new Customer(createCustomer());
        customerRepository.save(customer);
        Game game = new Game(createGame());
        gameRepository.save(game);
        customerRepository.delete(customer);

        RentalDTO rentalDTO = new RentalDTO(customer.getId(), game.getId(), 2L);

        HttpEntity<RentalDTO> body = new HttpEntity<>(rentalDTO);

        //when
        ResponseEntity<String> response = restTemplate.exchange(
            "/rentals", 
            HttpMethod.POST,
            body,
            String.class
            );
        
        //then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(0, rentalRepository.count());
        assertEquals("There is no customer with the informed id", response.getBody());
    }

    @Test
	void givenUnavailableGame_whenCreatingRental_throwError(){
        //given
        Customer customer = new Customer(createCustomer());
        customerRepository.save(customer);
        Game game = new Game(createGame());
        gameRepository.save(game);
        rentalRepository.save(new Rental(new RentalDTO(customer.getId(), game.getId(), 1L), customer, game));
        rentalRepository.save(new Rental(new RentalDTO(customer.getId(), game.getId(), 1L), customer, game));
        rentalRepository.save(new Rental(new RentalDTO(customer.getId(), game.getId(), 1L), customer, game));
        rentalRepository.save(new Rental(new RentalDTO(customer.getId(), game.getId(), 1L), customer, game));

        RentalDTO rentalDTO = new RentalDTO(customer.getId(), game.getId(), 2L);

        HttpEntity<RentalDTO> body = new HttpEntity<>(rentalDTO);

        //when
        ResponseEntity<String> response = restTemplate.exchange(
            "/rentals", 
            HttpMethod.POST,
            body,
            String.class
            );
        
        //then
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals(4, rentalRepository.count());
        assertEquals("All units of this game are already rented", response.getBody());
    }

    @Test
	void givenValidRental_whenFinishingRental_thenRentalIsFinished(){
        //given
        Customer customer = new Customer(createCustomer());
        customerRepository.save(customer);
        Game game = new Game(createGame());
        gameRepository.save(game);
        RentalDTO rentalDTO = new RentalDTO(customer.getId(), game.getId(), 2L);
        Rental rental = rentalRepository.save(new Rental(rentalDTO, customer, game));
        LocalDate today = LocalDate.now();

        //when
        ResponseEntity<Rental> response = restTemplate.exchange(
            "/rentals/{id}/return", 
            HttpMethod.PUT,
            null,
            Rental.class,
            rental.getId()
            );
        
        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(today, response.getBody().getReturnDate());
        assertEquals(0, response.getBody().getDelayFee());
    }

    @Test
	void givenValidRentalWithDelay_whenFinishingRental_thenRentalIsFinished(){
        //given
        Customer customer = new Customer(createCustomer());
        customerRepository.save(customer);
        Game game = new Game(createGame());
        gameRepository.save(game);
        LocalDate rentDate = LocalDate.now().minusDays(4);
        Rental rental = rentalRepository.save(new Rental(null, rentDate, 2L, null, 2000L, 0L, customer, game));
        LocalDate today = LocalDate.now();

        //when
        ResponseEntity<Rental> response = restTemplate.exchange(
            "/rentals/{id}/return", 
            HttpMethod.PUT,
            null,
            Rental.class,
            rental.getId()
            );
        
        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(today, response.getBody().getReturnDate());
        assertEquals(game.getPricePerDay() * 2L, response.getBody().getDelayFee());
    }

    @Test
	void givenFinishedRental_whenFinishingRental_thenThrowError(){
        //given
        Customer customer = new Customer(createCustomer());
        customerRepository.save(customer);
        Game game = new Game(createGame());
        gameRepository.save(game);
        LocalDate today = LocalDate.now();
        Rental rentalFinished = new Rental(null, today, 1L, today, 1000L, 0L, customer, game);
        rentalRepository.save(rentalFinished);

        //when
        ResponseEntity<String> response = restTemplate.exchange(
            "/rentals/{id}/return", 
            HttpMethod.PUT,
            null,
            String.class,
            rentalFinished.getId()
            );
        
        //then
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals("This rental is already finished", response.getBody());
    }

    @Test
	void givenNonExistingRentalId_whenFinishingRental_thenThrowError(){
        //given
        Customer customer = new Customer(createCustomer());
        customerRepository.save(customer);
        Game game = new Game(createGame());
        gameRepository.save(game);
        RentalDTO rentalDTO = new RentalDTO(customer.getId(), game.getId(), 2L);
        Rental rental = rentalRepository.save(new Rental(rentalDTO, customer, game));
        rentalRepository.save(rental);
        rentalRepository.delete(rental);

        //when
        ResponseEntity<String> response = restTemplate.exchange(
            "/rentals/{id}/return", 
            HttpMethod.PUT,
            null,
            String.class,
            rental.getId()
            );
        
        //then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("There is no rental with the informed id", response.getBody());
    }
}