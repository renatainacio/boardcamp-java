package com.boardcamp.api.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomerDTO {

    @NotBlank(message = "Name must not be blank")
    private String name;

    @NotNull(message = "CPF must not be null")
    @Size(min = 11, max = 11, message = "CPF must contain 11 digits")
    private String cpf;

}
