package com.boardcamp.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.boardcamp.api.dtos.CustomerDTO;
import com.boardcamp.api.dtos.GameDTO;
import com.boardcamp.api.dtos.RentalDTO;
import com.boardcamp.api.exceptions.CustomerNotFoundException;
import com.boardcamp.api.exceptions.GameNotFoundException;
import com.boardcamp.api.exceptions.NoUnitsAvailableException;
import com.boardcamp.api.exceptions.RentalAlreadyFinishedException;
import com.boardcamp.api.exceptions.RentalNotFoundException;
import com.boardcamp.api.models.Customer;
import com.boardcamp.api.models.Game;
import com.boardcamp.api.models.Rental;
import com.boardcamp.api.repositories.CustomerRepository;
import com.boardcamp.api.repositories.GameRepository;
import com.boardcamp.api.repositories.RentalRepository;
import com.boardcamp.api.services.RentalService;

@SpringBootTest
class RentalUnitTests {

	@InjectMocks
	private RentalService rentalService;

	@Mock
	private CustomerRepository customerRepository;

	@Mock
	private GameRepository gameRepository;

	@Mock
	private RentalRepository rentalRepository;

	CustomerDTO createCustomer(){
		String name = "John Doe";
		String cpf = "xxxxxxxxxxx";
		return new CustomerDTO(name, cpf);
	}

	GameDTO createGame(){
		String name = "name";
		String image = "image";
		Long stockTotal = 4L;
		Long pricePerDay = 1000L;
		return new GameDTO(name, image, stockTotal, pricePerDay);
	}
	
	
	RentalDTO createRental(){
		Long customerId = 1L;
		Long gameId = 2L;
		Long daysRented = 3L;
		return new RentalDTO(customerId, gameId, daysRented);
	}

	@Test
	void givenValidRental_whenCreatingRental_thenRentalIsCreated(){

		//given
		Game game = new Game(createGame());
		Customer customer = new Customer(createCustomer());
		RentalDTO rentalDTO = createRental();
		Rental newRental = new Rental(rentalDTO, customer, game);

		doReturn(Optional.of(customer)).when(customerRepository).findById(any());
		doReturn(Optional.of(game)).when(gameRepository).findById(any());
		doReturn(1L).when(rentalRepository).countUnavailableUnits(any());
		doReturn(newRental).when(rentalRepository).save(newRental);

		//when
		Rental rental = rentalService.postRental(rentalDTO);

		//then
		verify(customerRepository, times(1)).findById(any());
		verify(gameRepository, times(1)).findById(any());
		verify(rentalRepository, times(1)).countUnavailableUnits(any());
		verify(rentalRepository, times(1)).save(newRental);
		assertNotNull(rental);
		assertEquals(newRental, rental);
	}

	@Test
	void givenInvalidCustomerId_whenCreatingRental_thenThrowError(){
		//given
		Game game = new Game(createGame());
		Customer customer = new Customer(createCustomer());
		RentalDTO rentalDTO = createRental();
		Rental newRental = new Rental(rentalDTO, customer, game);

		doReturn(Optional.empty()).when(customerRepository).findById(any());
		doReturn(Optional.of(game)).when(gameRepository).findById(any());
		doReturn(1L).when(rentalRepository).countUnavailableUnits(any());
		doReturn(newRental).when(rentalRepository).save(newRental);

		//when
		CustomerNotFoundException exception = assertThrows(
			CustomerNotFoundException.class,
			() -> rentalService.postRental(rentalDTO));

		//then
		assertNotNull(exception);
		assertEquals("There is no customer with the informed id.", exception.getMessage());
		verify(customerRepository, times(1)).findById(any());
		verify(gameRepository, times(0)).findById(any());
		verify(rentalRepository, times(0)).countUnavailableUnits(any());
		verify(rentalRepository, times(0)).save(any());
	}

	@Test
	void givenInvalidGameId_whenCreatingRental_thenThrowError(){
		//given
		Game game = new Game(createGame());
		Customer customer = new Customer(createCustomer());
		RentalDTO rentalDTO = createRental();
		Rental newRental = new Rental(rentalDTO, customer, game);

		doReturn(Optional.of(customer)).when(customerRepository).findById(any());
		doReturn(Optional.empty()).when(gameRepository).findById(any());
		doReturn(1L).when(rentalRepository).countUnavailableUnits(any());
		doReturn(newRental).when(rentalRepository).save(newRental);

		//when
		GameNotFoundException exception = assertThrows(
			GameNotFoundException.class,
			() -> rentalService.postRental(rentalDTO));

		//then
		assertNotNull(exception);
		assertEquals("There is no game with the informed id.", exception.getMessage());
		verify(customerRepository, times(1)).findById(any());
		verify(gameRepository, times(1)).findById(any());
		verify(rentalRepository, times(0)).countUnavailableUnits(any());
		verify(rentalRepository, times(0)).save(any());
	}

	@Test
	void givenNoUnitsAvailable_whenCreatingRental_thenThrowError(){
		//given
		Game game = new Game(createGame());
		Customer customer = new Customer(createCustomer());
		RentalDTO rentalDTO = createRental();
		Rental newRental = new Rental(rentalDTO, customer, game);

		doReturn(Optional.of(customer)).when(customerRepository).findById(any());
		doReturn(Optional.of(game)).when(gameRepository).findById(any());
		doReturn(game.getStockTotal()).when(rentalRepository).countUnavailableUnits(any());
		doReturn(newRental).when(rentalRepository).save(newRental);

		//when
		NoUnitsAvailableException exception = assertThrows(
			NoUnitsAvailableException.class,
			() -> rentalService.postRental(rentalDTO));

		//then
		assertNotNull(exception);
		assertEquals("All units of this game are already rented.", exception.getMessage());
		verify(customerRepository, times(1)).findById(any());
		verify(gameRepository, times(1)).findById(any());
		verify(rentalRepository, times(1)).countUnavailableUnits(any());
		verify(rentalRepository, times(0)).save(any());
	}

	@Test
	void givenValidRentalWithNoDelay_whenFinishingRental_thenFinishRental(){
		//given
		LocalDate rentalDate = LocalDate.now();
		LocalDate finishDate = LocalDate.now().plusDays(2);
		Game game = new Game(createGame());
		Customer customer = new Customer(createCustomer());
		Rental openRental = new Rental(1L, rentalDate, 2L, null, 1500L, 0L, customer, game);
		Rental finishedRental = new Rental(1L, rentalDate, 2L, finishDate, 1500L, 0L, customer, game);

		doReturn(Optional.of(openRental)).when(rentalRepository).findById(any());
		doReturn(finishedRental).when(rentalRepository).save(openRental);

		//when
		Rental rental = rentalService.finishRental(openRental.getId(), finishDate);

		//then
		verify(rentalRepository, times(1)).findById(any());
		verify(rentalRepository, times(1)).save(any());
		assertNotNull(rental);
		assertEquals(finishedRental, rental);
		assertEquals(0, rental.getDelayFee());
		assertEquals(finishDate, rental.getReturnDate());
	}

	@Test
	void givenValidRentalWithDelay_whenFinishingRental_thenFinishRental(){
		//given
		LocalDate rentalDate = LocalDate.now();
		LocalDate finishDate = LocalDate.now().plusDays(4);
		Game game = new Game(createGame());
		Customer customer = new Customer(createCustomer());
		Rental openRental = new Rental(1L, rentalDate, 2L, null, 1500L, 0L, customer, game);
		Rental finishedRental = new Rental(1L, rentalDate, 2L, finishDate, 1500L, 3000L, customer, game);

		doReturn(Optional.of(openRental)).when(rentalRepository).findById(any());
		doReturn(finishedRental).when(rentalRepository).save(openRental);

		//when
		Rental rental = rentalService.finishRental(openRental.getId(), finishDate);

		//then
		verify(rentalRepository, times(1)).findById(any());
		verify(rentalRepository, times(1)).save(any());
		assertNotNull(rental);
		assertEquals(finishedRental, rental);
		assertEquals(3000, rental.getDelayFee());
		assertEquals(finishDate, rental.getReturnDate());
	}

	@Test
	void givenInvalidRentalId_whenFinishingRental_thenThrowError(){
		//given
		LocalDate rentalDate = LocalDate.now();
		LocalDate finishDate = LocalDate.now().plusDays(4);
		Game game = new Game(createGame());
		Customer customer = new Customer(createCustomer());
		Rental openRental = new Rental(1L, rentalDate, 2L, null, 1500L, 0L, customer, game);
		Rental finishedRental = new Rental(1L, rentalDate, 2L, finishDate, 1500L, 3000L, customer, game);

		doReturn(Optional.empty()).when(rentalRepository).findById(any());
		doReturn(finishedRental).when(rentalRepository).save(openRental);

		//when
		RentalNotFoundException exception = assertThrows(
			RentalNotFoundException.class,
			() -> rentalService.finishRental(openRental.getId(), finishDate));

		//then
		assertNotNull(exception);
		assertEquals("There is no rental with the informed id.", exception.getMessage());
		verify(rentalRepository, times(1)).findById(any());
		verify(rentalRepository, times(0)).save(any());
	}

	@Test
	void givenFinishedRental_whenFinishingRental_thenThrowError(){
		//given
		LocalDate rentalDate = LocalDate.now();
		LocalDate finishDate = LocalDate.now().plusDays(4);
		Game game = new Game(createGame());
		Customer customer = new Customer(createCustomer());
		Rental openRental = new Rental(1L, rentalDate, 2L, finishDate, 1500L, 0L, customer, game);
		Rental finishedRental = new Rental(1L, rentalDate, 2L, finishDate, 1500L, 3000L, customer, game);

		doReturn(Optional.of(openRental)).when(rentalRepository).findById(any());
		doReturn(finishedRental).when(rentalRepository).save(openRental);

		//when
		RentalAlreadyFinishedException exception = assertThrows(
			RentalAlreadyFinishedException.class,
			() -> rentalService.finishRental(openRental.getId(), finishDate));

		//then
		assertNotNull(exception);
		assertEquals("This rental is already finished.", exception.getMessage());
		verify(rentalRepository, times(1)).findById(any());
		verify(rentalRepository, times(0)).save(any());
	}

}