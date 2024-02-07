package com.boardcamp.api.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.boardcamp.api.dtos.RentalDTO;
import com.boardcamp.api.exceptions.CustomerNotFoundException;
import com.boardcamp.api.exceptions.GameNotFoundException;
import com.boardcamp.api.exceptions.NoUnitsAvailableException;
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

        int unavailableUnits = this.rentalRepository.countUnavailableUnits(game.get().getId());
        if (game.get().getStockTotal() <= unavailableUnits)
            throw new NoUnitsAvailableException();

        Rental rental = new Rental(rentalDTO, customer.get(), game.get());

        return this.rentalRepository.save(rental);
    }


    public List<Rental> getRentals(){
        return this.rentalRepository.findAll();
    }

}
