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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
		PaymentRequest paymentRequest = new PaymentRequest(
				new Payment(null, null, new BigDecimal("10.00"), Currency.EUR, "card", "Fee"));

		// Card is charged successfully
		given(cardPaymentCharger.chargeCard(paymentRequest.getPayment().getSource(),
				paymentRequest.getPayment().getAmount(), paymentRequest.getPayment().getCurrency(),
				paymentRequest.getPayment().getDescription())).willReturn(new CardPaymentCharge(true));

		// When
		underTest.chargeCard(customerId, paymentRequest);

		// Then
		ArgumentCaptor<Payment> paymentArgumentCaptor = ArgumentCaptor.forClass(Payment.class);

		then(paymentRepository).should().save(paymentArgumentCaptor.capture());

		Payment paymentArgumentCaptorValue = paymentArgumentCaptor.getValue();

		assertThat(paymentArgumentCaptorValue).usingRecursiveComparison().ignoringFields("customerId")
				.isEqualTo(paymentRequest.getPayment());

		assertThat(paymentArgumentCaptorValue.getCustomerId()).isEqualTo(customerId);
	}

	@Test
	void itShouldThrowWhenCardIsNotCharged() {
		// Given
		UUID customerId = UUID.randomUUID();

		// customer exists
		given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class)));

		// payment request
		PaymentRequest paymentRequest = new PaymentRequest(
				new Payment(null, null, new BigDecimal("50.00"), Currency.EUR, "card", "Fee"));

		// card is not charged successfully
		given(cardPaymentCharger.chargeCard(paymentRequest.getPayment().getSource(),
				paymentRequest.getPayment().getAmount(), paymentRequest.getPayment().getCurrency(),
				paymentRequest.getPayment().getDescription())).willReturn(new CardPaymentCharge(false));

		// When
		// Then
		assertThatThrownBy(() -> underTest.chargeCard(customerId, paymentRequest))
				.isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("Card can not debited for customer:" + customerId);

		// no interaction with paymentRepository
		then(paymentRepository).shouldHaveNoInteractions();
	}
	
	@Test
	void itShouldNotChargeCardAndThrowWhenCurrencyNotSupported() {
		// Given
		UUID customerId = UUID.randomUUID();
				
		given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class)));
		
		Currency currency = Currency.GBP;
		
		// payment request
		PaymentRequest paymentRequest = new PaymentRequest(
					new Payment(null, null, new BigDecimal("50.00"), currency, "card", "Fee"));		

		// When
		assertThatThrownBy(() -> underTest.chargeCard(customerId, paymentRequest))
			.isInstanceOf(IllegalStateException.class)
			.hasMessageContaining(String.format("Currency is not supported:%s", currency));
		
		// Then
		
		// no interaction with cardPaymentCharger
		then(cardPaymentCharger).shouldHaveNoInteractions();
		
		// no interaction with paymentRepository
		then(paymentRepository).shouldHaveNoInteractions();
	}
	
	@Test
	void itShouldNotChargeAndThrowWhenCustomerNotFound()
	{
		// Given
		UUID customerId = UUID.randomUUID();
		// customer not found
		given(customerRepository.findById(customerId)).willReturn(Optional.empty());
		
		// When
		assertThatThrownBy(() -> underTest.chargeCard(customerId, new PaymentRequest(new Payment())))
			.isInstanceOf(IllegalStateException.class)
			.hasMessageContaining(String.format("Customer with id not found:%s", customerId));
			
		// Then
		// no interaction
		then(cardPaymentCharger).shouldHaveNoInteractions();
		then(paymentRepository).shouldHaveNoInteractions();
	}
}
