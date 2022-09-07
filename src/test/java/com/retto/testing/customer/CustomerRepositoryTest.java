package com.retto.testing.customer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

@DataJpaTest(properties = { "spring.jpa.properties.javax.persistence.validation.mode=none" })
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
	void itShouldNotFindCustomerbyPhoneNumber() {
		// Given
		String phoneNumber = "1234";

		// When
		Optional<Customer> optionalCustomer = underTest.findByPhoneNumber(phoneNumber);

		// Then
		assertThat(optionalCustomer).isNotPresent();
	}

	@Test
	void itShouldSaveCustomer() {
		// Given
		UUID id = UUID.randomUUID();
		String phoneNumber = "1234";
		Customer customer = new Customer(id, "Retto", phoneNumber);

		// When
		underTest.save(customer);

		// Then
		Optional<Customer> optionalCustomer = underTest.findById(id);
		assertThat(optionalCustomer).isPresent().hasValueSatisfying(c -> assertThat(c).isEqualTo(customer));
	}

	@Test
	void itShouldNotSaveCustomerWhenNameIsNull() {
		// Given
		UUID id = UUID.randomUUID();
		Customer customer = new Customer(id, null, "1234");

		// When
		// Then
		assertThatThrownBy(() -> underTest.save(customer)).hasMessageContaining(
				"not-null property references a null or transient value : com.retto.testing.customer.Customer.name")
				.isInstanceOf(DataIntegrityViolationException.class);
	}

	@Test
	void itShouldNotSaveCustomerWhenPhoneNumberIsNull() {
		// Given
		UUID id = UUID.randomUUID();
		Customer customer = new Customer(id, "Retto", null);

		// When
		// Then
		assertThatThrownBy(() -> underTest.save(customer)).hasMessageContaining(
				"not-null property references a null or transient value : com.retto.testing.customer.Customer.phoneNumber")
				.isInstanceOf(DataIntegrityViolationException.class);
	}
}
