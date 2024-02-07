package com.boardcamp.api.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CustomerDTO {

    @NotBlank(message = "Name must not be blank")
    private String name;

    @NotBlank(message = "CPF must not be blank")
    @Size(min = 11, max = 11, message = "CPF must contain 11 digits")
    private String cpf;

}
