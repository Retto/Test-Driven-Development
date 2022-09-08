package com.retto.testing.payment;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

import com.retto.testing.customer.Customer;
import com.retto.testing.customer.CustomerRepository;

public class PaymentServiceTest {
	
	@Mock
	private CustomerRepository customerRepository;
	
	@Mock
	private PaymentRepository paymentRepository;
	
	@Mock
	private CardPaymentCharger cardPaymentCharger;
	
	private PaymentService underTest;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		underTest = new PaymentService(customerRepository, paymentRepository, cardPaymentCharger);
	}
	
	@Test
	void itShouldChargeCardSuccessfully() {
		// Given
		UUID customerId = UUID.randomUUID();

		// check customer exists
		given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class)));
		
		// payment request
		PaymentRequest paymentRequest = new PaymentRequest(new Payment( 
				null,
				null,
				new BigDecimal("10.00"),
				Currency.EUR,
				"card",
				"Fee"
				));
		
		// Card is charged successfully
		given(cardPaymentCharger.chargeCard(
				paymentRequest.getPayment().getSource(), 
				paymentRequest.getPayment().getAmount(),
				paymentRequest.getPayment().getCurrency(),
				paymentRequest.getPayment().getDescription()
				)).willReturn(new CardPaymentCharge(true));
		
		// When
		underTest.chargeCard(customerId, paymentRequest);
		
		// Then
		ArgumentCaptor<Payment> paymentArgumentCaptor = ArgumentCaptor.forClass(Payment.class);
		
		then(paymentRepository).should().save(paymentArgumentCaptor.capture());
		
		Payment paymentArgumentCaptorValue = paymentArgumentCaptor.getValue();

		assertThat(paymentArgumentCaptorValue).usingRecursiveComparison().ignoringFields("customerId").isEqualTo(paymentRequest.getPayment());
		
		assertThat(paymentArgumentCaptorValue.getCustomerId()).isEqualTo(customerId);
	}
}
