package com.retto.testing.payment.stripe;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;

@Service
public class StripeApi {

	public Charge create(Map<String, Object> requestMap, RequestOptions options) throws StripeException {
		return Charge.create(requestMap, options);
	}
}
