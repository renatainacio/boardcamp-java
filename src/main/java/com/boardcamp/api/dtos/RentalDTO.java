package com.boardcamp.api.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class RentalDTO {

    @NotNull
    @Positive(message = "customerId should be greater than zero.")
    private Long customerId;

    @NotNull
    @Positive(message = "gameId should be greater than zero.")
    private Long gameId;

    @NotNull
    @Positive(message = "daysRented should be greater than zero.")
    private int daysRented;

}
