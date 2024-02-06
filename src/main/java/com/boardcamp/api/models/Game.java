package com.boardcamp.api.models;

import com.boardcamp.api.dtos.GameDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "games")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column
    private String image;

    @Column
    private int stockTotal;

    @Column
    private int pricePerDay;

    public Game(GameDTO gameDTO){
        this.image = gameDTO.getImage();
        this.name = gameDTO.getName();
        this.pricePerDay = gameDTO.getPricePerDay();
        this.stockTotal = gameDTO.getStockTotal();
    }
}
