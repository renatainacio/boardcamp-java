package com.boardcamp.api.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GameDTO {

    @NotBlank(message = "Name must not be blank")
    private String name;

    private String image;

    @NotNull(message = "Stock Total must not be null")
    @Min(value = 1, message = "stockTotal should be greater than zero.")
    private int stockTotal;

    @NotNull(message = "Price Per Day must not be null")
    @Min(value = 1, message = "pricePerDay should be greater than zero.")
    private int pricePerDay;
}
