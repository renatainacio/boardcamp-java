package com.boardcamp.api.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GameDTO {

    @NotBlank(message = "Name must not be blank")
    private String name;

    private String image;

    @NotNull(message = "Stock Total must not be null")
    @Positive(message = "pricePerDay should be greater than zero.")
    private Long stockTotal;

    @NotNull(message = "Price Per Day must not be null")
    @Positive(message = "pricePerDay should be greater than zero.")
    private Long pricePerDay;
}
