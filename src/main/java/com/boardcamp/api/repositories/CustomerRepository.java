package com.boardcamp.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.boardcamp.api.models.Customer;
import java.util.List;


public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByCpf(String cpf);
}
