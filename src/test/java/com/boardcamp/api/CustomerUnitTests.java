package com.boardcamp.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.boardcamp.api.dtos.CustomerDTO;
import com.boardcamp.api.exceptions.CPFAlreadyRegisteredException;
import com.boardcamp.api.exceptions.CustomerNotFoundException;
import com.boardcamp.api.models.Customer;
import com.boardcamp.api.repositories.CustomerRepository;
import com.boardcamp.api.services.CustomerService;

@SpringBootTest
class CustomerUnitTests {

	@InjectMocks
	private CustomerService customerService;

	@Mock
	private CustomerRepository customerRepository;

	CustomerDTO createCustomer(){
		String name = "John Doe";
		String cpf = "xxxxxxxxxxx";
		return new CustomerDTO(name, cpf);
	}

	@Test
	void givenValidCustomer_whenCreatingCustomer_thenCustomerIsCreated(){

		//given
		CustomerDTO customerDTO = createCustomer();
		Customer newCustomer = new Customer(customerDTO);

		doReturn(false).when(customerRepository).existsByCpf(any());
		doReturn(newCustomer).when(customerRepository).save(newCustomer);

		//when
		Customer customer = customerService.postCustomer(customerDTO);

		//then
		verify(customerRepository, times(1)).existsByCpf(customerDTO.getCpf());
		verify(customerRepository, times(1)).save(any());
		assertNotNull(customer);
		assertEquals(newCustomer, customer);
	}

	@Test
	void givenRepeatedCPF_whenCreatingCustomer_thenThrowError(){
		//given
		CustomerDTO customerDTO = createCustomer();
		Customer newCustomer = new Customer(customerDTO);

		doReturn(true).when(customerRepository).existsByCpf(any());
		doReturn(newCustomer).when(customerRepository).save(newCustomer);

		//when
		CPFAlreadyRegisteredException exception = assertThrows(
			CPFAlreadyRegisteredException.class,
			() -> customerService.postCustomer(customerDTO));

		//then
		assertNotNull(exception);
		assertEquals("The informed CPF is already registered", exception.getMessage());
		verify(customerRepository, times(1)).existsByCpf(customerDTO.getCpf());
		verify(customerRepository, times(0)).save(any());
	}

	@Test
	void givenValidCustomerId_whenGettingCustomerById_thenCustomerIsReturned(){

		//given
		CustomerDTO customerDTO = createCustomer();
		Customer customer = new Customer(customerDTO);

		doReturn(Optional.of(customer)).when(customerRepository).findById(customer.getId());

		//when
		Customer customerFound = customerService.getCustomerById(customer.getId());

		//then
		verify(customerRepository, times(1)).findById(any());
		assertNotNull(customerFound);
		assertEquals(customer, customerFound);
	}

	@Test
	void givenExistingCPF_whenGettingCustomerById_thenThrowError(){

		//given
		CustomerDTO customerDTO = createCustomer();
		Customer customer = new Customer(customerDTO);

		doReturn(Optional.empty()).when(customerRepository).findById(customer.getId());

		//when
		CustomerNotFoundException exception = assertThrows(
			CustomerNotFoundException.class,
			() -> customerService.getCustomerById(customer.getId()));

		//then
		verify(customerRepository, times(1)).findById(any());
		assertNotNull(exception);
		assertEquals("There is no customer with the informed id", exception.getMessage());
	}

}
