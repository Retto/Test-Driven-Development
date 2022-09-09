package com.retto.testing.customer;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID>{

	@Query(value = "SELECT id, name, phone_number FROM customer WHERE phone_number = :phone_number",
			nativeQuery = true)
	Optional<Customer> findByPhoneNumber(@Param("phone_number") String phoneNumber);
}
