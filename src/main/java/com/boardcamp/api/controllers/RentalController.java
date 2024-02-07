package com.boardcamp.api.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.boardcamp.api.dtos.RentalDTO;
import com.boardcamp.api.models.Rental;
import com.boardcamp.api.services.RentalService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/rentals")
public class RentalController {
    
    private final RentalService rentalService;

    RentalController(RentalService rentalService){
        this.rentalService = rentalService;
    }

    @PostMapping
    public ResponseEntity<Rental> postRental(@RequestBody @Valid RentalDTO rentalDTO){
        Rental rental = this.rentalService.postRental(rentalDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(rental);
    }

    @GetMapping
    public ResponseEntity<List<Rental>> getRentals(){
        List<Rental> rentals = this.rentalService.getRentals();
        return ResponseEntity.status(HttpStatus.OK).body(rentals);
    }

    @PutMapping("/{id}/return")
    public ResponseEntity<Rental> finishRental(@PathVariable Long id){
        Rental rental = this.rentalService.finishRental(id, LocalDate.now());
        return ResponseEntity.status(HttpStatus.OK).body(rental);
    }
}
