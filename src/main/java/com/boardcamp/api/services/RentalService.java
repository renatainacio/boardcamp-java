package com.boardcamp.api.services;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

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

@Service
public class RentalService {

    private final RentalRepository rentalRepository;
    private final CustomerRepository customerRepository;
    private final GameRepository gameRepository;

    RentalService(RentalRepository rentalRepository, CustomerRepository customerRepository, GameRepository gameRepository){
        this.rentalRepository = rentalRepository;
        this.customerRepository = customerRepository;
        this.gameRepository = gameRepository;
    }

    public Rental postRental(RentalDTO rentalDTO){
        Optional<Customer> customer = this.customerRepository.findById(rentalDTO.getCustomerId());
        if(!customer.isPresent())
            throw new CustomerNotFoundException();
        
        Optional<Game> game = this.gameRepository.findById(rentalDTO.getGameId());
        if(!game.isPresent())
            throw new GameNotFoundException();

        Long unavailableUnits = this.rentalRepository.countUnavailableUnits(game.get().getId());
        if (game.get().getStockTotal() <= unavailableUnits)
            throw new NoUnitsAvailableException();

        Rental rental = new Rental(rentalDTO, customer.get(), game.get());

        return this.rentalRepository.save(rental);
    }


    public List<Rental> getRentals(){
        return this.rentalRepository.findAll();
    }

    public Rental finishRental(Long id, LocalDate today){
        Optional<Rental> rental = this.rentalRepository.findById(id);
        if(!rental.isPresent())
            throw new RentalNotFoundException();
        
        if (rental.get().getReturnDate() != null)
            throw new RentalAlreadyFinishedException();

        rental.get().setReturnDate(today);
        long rentalDays = ChronoUnit.DAYS.between(rental.get().getRentDate(), today);
        
        if (rentalDays > rental.get().getDaysRented())
            rental.get().setDelayFee(calculateDelayFee(rental.get(), rentalDays));

        return this.rentalRepository.save(rental.get());
    }

    private long calculateDelayFee(Rental rental, long rentalDays){
        return ((rentalDays - rental.getDaysRented()) * rental.getGame().getPricePerDay());
    }

}
