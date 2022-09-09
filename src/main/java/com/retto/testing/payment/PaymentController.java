package com.retto.testing.payment;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {
	
	private final PaymentService paymentService;
	
	@PostMapping
	public void makePayment(
			@RequestBody PaymentRequest paymentRequest
			) {
		paymentService.chargeCard(paymentRequest.getPayment().getCustomerId(), paymentRequest);
	}
}
