package com.boardcamp.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.boardcamp.api.dtos.CustomerDTO;
import com.boardcamp.api.models.Customer;
import com.boardcamp.api.repositories.CustomerRepository;
import com.boardcamp.api.repositories.RentalRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CustomerIntegrationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RentalRepository rentalRepository;

    @BeforeEach
    private void cleanupDatabase(){
        rentalRepository.deleteAll();
        customerRepository.deleteAll();
    }

    CustomerDTO createCustomer(){
		String name = "John Doe";
		String cpf = "xxxxxxxxxxx";
		return new CustomerDTO(name, cpf);
	}

    @Test
	void givenValidCustomerData_whenCreatingCustomer_thenCustomerIsCreated(){
        //given
        CustomerDTO customerDTO = createCustomer();
        HttpEntity<CustomerDTO> body = new HttpEntity<>(customerDTO);

        //when
        ResponseEntity<Customer> response = restTemplate.exchange(
            "/customers", 
            HttpMethod.POST,
            body,
            Customer.class
            );
        
        //then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(1, customerRepository.count());
        assertEquals(customerDTO.getName(), response.getBody().getName());
        assertEquals(customerDTO.getCpf(), response.getBody().getCpf());
    }

    @Test
	void givenNullName_whenCreatingCustomer_thenThrowError(){
        //given
        CustomerDTO customerDTO = new CustomerDTO(null, "xxxxxxxxxxx");
        HttpEntity<CustomerDTO> body = new HttpEntity<>(customerDTO);

        //when
        ResponseEntity<String> response = restTemplate.exchange(
            "/customers", 
            HttpMethod.POST,
            body,
            String.class
            );
        
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(0, customerRepository.count());
        assertEquals("Name must not be blank", response.getBody());
    }

    @Test
	void givenBlankName_whenCreatingCustomer_thenThrowError(){
        //given
        CustomerDTO customerDTO = new CustomerDTO("", "xxxxxxxxxxx");
        HttpEntity<CustomerDTO> body = new HttpEntity<>(customerDTO);

        //when
        ResponseEntity<String> response = restTemplate.exchange(
            "/customers", 
            HttpMethod.POST,
            body,
            String.class
            );
        
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(0, customerRepository.count());
        assertEquals("Name must not be blank", response.getBody());
    }

    @Test
	void givenNullCPF_whenCreatingCustomer_thenThrowError(){
        //given
        CustomerDTO customerDTO = new CustomerDTO("John Doe", null);
        HttpEntity<CustomerDTO> body = new HttpEntity<>(customerDTO);

        //when
        ResponseEntity<String> response = restTemplate.exchange(
            "/customers", 
            HttpMethod.POST,
            body,
            String.class
            );
        
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(0, customerRepository.count());
        assertEquals("CPF must not be null", response.getBody());
    }

    @Test
	void givenBlankCPF_whenCreatingCustomer_thenThrowError(){
        //given
        CustomerDTO customerDTO = new CustomerDTO("John Doe", "");
        HttpEntity<CustomerDTO> body = new HttpEntity<>(customerDTO);

        //when
        ResponseEntity<String> response = restTemplate.exchange(
            "/customers", 
            HttpMethod.POST,
            body,
            String.class
            );
        
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(0, customerRepository.count());
        assertEquals("CPF must contain 11 digits", response.getBody());
    }

    @Test
	void givenShorterCPF_whenCreatingCustomer_thenThrowError(){
        //given
        CustomerDTO customerDTO = new CustomerDTO("John Doe", "xxxxxxxxxx");
        HttpEntity<CustomerDTO> body = new HttpEntity<>(customerDTO);

        //when
        ResponseEntity<String> response = restTemplate.exchange(
            "/customers", 
            HttpMethod.POST,
            body,
            String.class
            );
        
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(0, customerRepository.count());
        assertEquals("CPF must contain 11 digits", response.getBody());
    }

    @Test
	void givenLongerCPF_whenCreatingCustomer_thenThrowError(){
        //given
        CustomerDTO customerDTO = new CustomerDTO("John Doe", "xxxxxxxxxxxx");
        HttpEntity<CustomerDTO> body = new HttpEntity<>(customerDTO);

        //when
        ResponseEntity<String> response = restTemplate.exchange(
            "/customers", 
            HttpMethod.POST,
            body,
            String.class
            );
        
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(0, customerRepository.count());
        assertEquals("CPF must contain 11 digits", response.getBody());
    }

    @Test
	void givenDuplicatedCPF_whenCreatingCustomer_thenThrowError(){
        //given
        CustomerDTO customerDTO = createCustomer();
        customerRepository.save(new Customer(customerDTO));
        HttpEntity<CustomerDTO> body = new HttpEntity<>(customerDTO);

        //when
        ResponseEntity<String> response = restTemplate.exchange(
            "/customers", 
            HttpMethod.POST,
            body,
            String.class
            );
        
        //then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(1, customerRepository.count());
        assertEquals("The informed CPF is already registered", response.getBody());
    }


    @Test
	void givenNonExistingId_whenGettingCustomer_thenThrowError(){
        //given
        CustomerDTO customerDTO = createCustomer();
        Customer customer = customerRepository.save(new Customer(customerDTO));
        customerRepository.delete(customer);

        //when
        ResponseEntity<String> response = restTemplate.exchange(
            "/customers/{id}", 
            HttpMethod.GET,
            null,
            String.class,
            customer.getId()
            );
        
        //then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("There is no customer with the informed id", response.getBody());
    }

    @Test
	void givenExistingId_whenGettingCustomer_thenReturnCustomer(){
        //given
        CustomerDTO customerDTO = createCustomer();
        Customer customer = customerRepository.save(new Customer(customerDTO));

        //when
        ResponseEntity<Customer> response = restTemplate.exchange(
            "/customers/{id}", 
            HttpMethod.GET,
            null,
            Customer.class,
            customer.getId()
            );
        
        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(customer, response.getBody());
    }
}