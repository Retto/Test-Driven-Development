package com.retto.testing.payment.stripe;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.retto.testing.payment.CardPaymentCharge;
import com.retto.testing.payment.Currency;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;

public class StripeServiceTest {

	private StripeService underTest;
	
	@Mock
	private StripeApi stripeApi;
	
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		underTest = new StripeService(stripeApi); 
	}
	
	@Test
	void itShouldChargeCard() throws StripeException {
		// Given
        String cardSource = "123456";
        BigDecimal amount = new BigDecimal("99.00");
        Currency currency = Currency.USD;
        String description = "Donation";
		
		// successful charge
		Charge charge = new Charge();
		charge.setPaid(true);
		
		given(stripeApi.create(anyMap(), any())).willReturn(charge);
		
		// When
		CardPaymentCharge cardPaymentCharge = underTest.chargeCard(cardSource, amount, currency, description);
		
		// Then
		ArgumentCaptor<Map<String, Object>> mapArgumentCaptor = ArgumentCaptor.forClass(Map.class);
		ArgumentCaptor<RequestOptions> optionsArgumentCaptor = ArgumentCaptor.forClass(RequestOptions.class);
		
		// Captor requestMap and options
		then(stripeApi).should().create(mapArgumentCaptor.capture(), optionsArgumentCaptor.capture());
		
		// Assert on requestMap
		Map<String, Object> requestMap = mapArgumentCaptor.getValue();
		
		assertThat(requestMap.keySet()).hasSize(4);
		
        assertThat(requestMap.get("amount")).isEqualTo(amount);
        assertThat(requestMap.get("currency")).isEqualTo(currency);
        assertThat(requestMap.get("source")).isEqualTo(cardSource);
        assertThat(requestMap.get("description")).isEqualTo(description);
		
        // assert on options
        RequestOptions options = optionsArgumentCaptor.getValue();
        assertThat(options).isNotNull();
        
        // card is debited successfully
        assertThat(cardPaymentCharge).isNotNull();
        assertThat(cardPaymentCharge.isCardDebited()).isTrue();
	}
	
	@Test
	void itShouldNotChargeWhenApiThrowsException() throws StripeException {
		// Given
        String cardSource = "123456";
        BigDecimal amount = new BigDecimal("99.00");
        Currency currency = Currency.USD;
        String description = "Donation";
		
		// unsuccessful charge
		Charge charge = new Charge();
		charge.setPaid(false);
		
		StripeException stripeException = mock(StripeException.class);
		given(stripeApi.create(anyMap(), any())).willThrow(stripeException);
		// doThrow(mock(StripeException.class)).when(stripeApi).create(anyMap(), any());
		
		// When
		// Then
		assertThatThrownBy(() -> underTest.chargeCard(cardSource, amount, currency, description))
			.isInstanceOf(IllegalStateException.class)
			.hasRootCause(stripeException)
			.hasMessageContaining("Can not make stripe charge");
	}
}
