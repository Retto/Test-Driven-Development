package com.retto.testing.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.retto.testing.customer.Customer;
import com.retto.testing.customer.CustomerRegistrationRequest;

@SpringBootTest
@AutoConfigureMockMvc
public class PaymentIntegrationTest {

	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	private MockMvc mockMvc;

	/* 
	 * Without @transactional annotation. 
	 * We receives Customer with id not found exception. 
	 * Reason: transaction does not commit yet when the test method runs. So, the repository customerRepository.findById() method call returns null.
     * We need a transactional request if the pom version is equal to 2.7.3. 
	 * */
	@Transactional
	@Test
	void itShouldCreatePaymentSuccessfully() throws Exception {
		// Given
		// create customer
		UUID customerId = UUID.randomUUID();
		Customer customer = new Customer(customerId, "Retto", "+170000000000");

		CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(customer);

		// register
		ResultActions customerRegisterResultActions = mockMvc
				.perform(put("/api/v1/customer").contentType(MediaType.APPLICATION_JSON)
						.content(Objects.requireNonNull(objectToJson(customerRegistrationRequest))));

		// Payment
		long paymentId = 1L;

		Payment payment = new Payment(paymentId, customerId, new BigDecimal("100.00"), Currency.EUR, "123456",
				"Donation");

		// Payment request
		PaymentRequest paymentRequest = new PaymentRequest(payment);

		// When payment is sent
		ResultActions paymentResultActions = mockMvc.perform(
				post("/api/v1/payment").contentType(MediaType.APPLICATION_JSON).content(objectToJson(paymentRequest)));

		// Then both customer registration and payment requests are 200 status code
		customerRegisterResultActions.andExpect(status().isOk());
		paymentResultActions.andExpect(status().isOk());
		
		// Payment is stored in DB

        assertThat(paymentRepository.findById(paymentId))
                .isPresent()
                .hasValueSatisfying(p -> assertThat(p).usingRecursiveComparison().isEqualTo(payment));
	}

	private String objectToJson(Object object) {
		try {
			return new ObjectMapper().writeValueAsString(object);
		} catch (JsonProcessingException e) {
			fail("Failed to convert object to json");
			return null;
		}
	}
}
