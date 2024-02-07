package com.boardcamp.api.models;

import java.time.LocalDate;

import com.boardcamp.api.dtos.RentalDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "rentals")
@NoArgsConstructor
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column
    private LocalDate rentDate;

    @Column
    private int daysRented;

    @Column
    private LocalDate returnDate;

    @Column
    private int originalPrice;

    @Column
    private int delayFee;

    @ManyToOne
    @JoinColumn(name = "customerId")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "gameId")
    private Game game;

    public Rental(RentalDTO rentalDTO, Customer customer, Game game){
        this.rentDate = LocalDate.now();
        this.daysRented = rentalDTO.getDaysRented();
        this.returnDate = null;
        this.originalPrice = game.getPricePerDay() * daysRented;
        this.delayFee = 0;
        this.customer = customer;
        this.game = game;
    }

}
