package com.retto.testing.payment.stripe;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.retto.testing.payment.CardPaymentCharge;
import com.retto.testing.payment.CardPaymentCharger;
import com.retto.testing.payment.Currency;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StripeService implements CardPaymentCharger {

	private final StripeApi stripeApi;
	
	private final static RequestOptions REQUEST_OPTIONS = RequestOptions.builder().setApiKey("sk_test_4eC39HqLyjWDarjtT1zdp7dc").build();
	
	@Override
	public CardPaymentCharge chargeCard(String cardSource, BigDecimal amount, Currency currency, String description) {
		
		Map<String, Object> params = new HashMap<>();
		params.put("amount", amount);
		params.put("currency", currency);
		params.put("source", cardSource);
		params.put("description", description);
		
		try {
			Charge charge = stripeApi.create(params, REQUEST_OPTIONS);
			return new CardPaymentCharge(charge.getPaid());			
		} catch (StripeException e) {
			throw new IllegalStateException("Can not make stripe charge", e);
		}
	}
}
