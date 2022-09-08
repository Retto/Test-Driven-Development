package com.retto.testing.payment;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest(
		properties = {
				"spring.jpa.properties.javax.persistence.validation.mode=none"
		}
)
public class PaymentRepositoryTest {

	@Autowired
	private PaymentRepository underTest;
	
	@Test
	void itShouldInsertPayment() {
		// Given
		Payment payment = new Payment(
				null,
				UUID.randomUUID(),
				new BigDecimal("10.00"),
				Currency.USD,
				"card",
				"Donation"
				);
		
		// When
		underTest.save(payment);
		
		// Then
		Optional<Payment> paymentOptional = underTest.findById(1L);
		assertThat(paymentOptional).isPresent().hasValueSatisfying( p -> assertThat(p).isEqualTo(payment));
	}
}
