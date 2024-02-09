package com.boardcamp.api.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RentalDTO {

    @NotNull(message = "Customer Id must not be null")
    @Positive(message = "Customer Id should be greater than zero")
    private Long customerId;

    @NotNull(message = "Game Id must not be null")
    @Positive(message = "Game Id should be greater than zero")
    private Long gameId;

    @NotNull(message = "Days Rented must not be null")
    @Positive(message = "Days Rented should be greater than zero")
    private Long daysRented;

}
