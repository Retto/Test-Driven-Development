package com.retto.testing.customer;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest(
		properties = {
				"spring.jpa.properties.javax.persistence.validation.mode=none"
		}
)
public class CustomerRepositoryTest {

	@Autowired
	private CustomerRepository underTest;
	
	@Test
	void itShouldFindCustomerbyPhoneNumber() {
		
		// Given
		UUID id = UUID.randomUUID();
		String phoneNumber = "1234";
		Customer customer = new Customer(id, "Retto", phoneNumber);
				
		// When
		underTest.save(customer);
		
		// Then
		Optional<Customer> optionalCustomer = underTest.findByPhoneNumber(phoneNumber);
		
		assertThat(optionalCustomer).isPresent().hasValueSatisfying(c -> assertThat(c).isEqualTo(customer));
	}
	
	@Test
	@DisplayName("Should NOT find customer by phone number")
	void itShouldNotFindCustomerbyPhoneNumber()
	{
		// Given
        String phoneNumber = "1234";

        // When
        Optional<Customer> optionalCustomer = underTest.findByPhoneNumber(phoneNumber);

        // Then
        assertThat(optionalCustomer).isNotPresent();
	}
}
