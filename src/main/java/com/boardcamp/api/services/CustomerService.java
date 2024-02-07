package com.boardcamp.api.services;

import org.springframework.stereotype.Service;

import com.boardcamp.api.dtos.CustomerDTO;
import com.boardcamp.api.exceptions.CPFAlreadyRegisteredException;
import com.boardcamp.api.models.Customer;
import com.boardcamp.api.models.Game;
import com.boardcamp.api.repositories.CustomerRepository;

@Service
public class CustomerService {
    
    private final CustomerRepository customerRepository;

    CustomerService(CustomerRepository customerRepository){
        this.customerRepository = customerRepository;
    }

    public Customer postCustomer(CustomerDTO customerDTO){

        boolean existingCustomer = !this.customerRepository.findByCpf(customerDTO.getCpf()).isEmpty();

        if (existingCustomer) 
            throw new CPFAlreadyRegisteredException("The informed CPF is already registered.");
        else {
            Customer customer = new Customer(customerDTO);

            return this.customerRepository.save(customer);
        }
    }

}
