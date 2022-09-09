package com.retto.testing.payment.stripe;

import java.math.BigDecimal;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.retto.testing.payment.CardPaymentCharge;
import com.retto.testing.payment.CardPaymentCharger;
import com.retto.testing.payment.Currency;

@Service
@ConditionalOnProperty(
        value = "stripe.enabled",
        havingValue = "false"
)
public class MockStripeService implements CardPaymentCharger {

	
	@Override
	public CardPaymentCharge chargeCard(String cardSource, BigDecimal amount, Currency currency, String description) {
		return new CardPaymentCharge(true);
	}
}
