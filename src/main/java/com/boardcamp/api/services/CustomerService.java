package com.boardcamp.api.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.boardcamp.api.dtos.CustomerDTO;
import com.boardcamp.api.exceptions.CPFAlreadyRegisteredException;
import com.boardcamp.api.exceptions.CustomerNotFoundException;
import com.boardcamp.api.models.Customer;
import com.boardcamp.api.repositories.CustomerRepository;

@Service
public class CustomerService {
    
    private final CustomerRepository customerRepository;

    CustomerService(CustomerRepository customerRepository){
        this.customerRepository = customerRepository;
    }

    public Customer postCustomer(CustomerDTO customerDTO){

        boolean existingCustomer = this.customerRepository.existsByCpf(customerDTO.getCpf());

        if (existingCustomer) 
            throw new CPFAlreadyRegisteredException();
        else {
            Customer customer = new Customer(customerDTO);

            return this.customerRepository.save(customer);
        }
    }

    public Customer getCustomerById(Long id){
        Optional<Customer> customer = this.customerRepository.findById(id);
        if (!customer.isPresent())
            throw new CustomerNotFoundException();
        else
            return customer.get();
    }

}
