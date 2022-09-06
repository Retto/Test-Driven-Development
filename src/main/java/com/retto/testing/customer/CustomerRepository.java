package com.retto.testing.customer;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CustomerRepository extends JpaRepository<Customer, UUID>{

	@Query(value = "SELECT id, name, phone_number FROM customer WHERE phone_number = :phoneNumber",
			nativeQuery = true)
	Optional<Customer> findByPhoneNumber(String phoneNumber);
}
