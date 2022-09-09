package com.retto.testing.payment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.retto.testing.customer.Customer;
import com.retto.testing.customer.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Service
// @RequiredArgsConstructor
public class PaymentService {

	private static final List<Currency> ACCEPTED_CURRENCIES = List.of(Currency.EUR, Currency.USD);
	
	private final CustomerRepository customerRepository;
	private final PaymentRepository paymentRepository;
	private final CardPaymentCharger cardPaymentCharger;

    @Autowired
    public PaymentService(CustomerRepository customerRepository,
                          PaymentRepository paymentRepository,
                          CardPaymentCharger cardPaymentCharger) {
        this.customerRepository = customerRepository;
        this.paymentRepository = paymentRepository;
        this.cardPaymentCharger = cardPaymentCharger;
    }
    
	void chargeCard(UUID customerId, PaymentRequest paymentRequest) {
		// 1. check customer exists
		Optional<Customer> customerOptional = customerRepository.findById(customerId);
		if(!customerOptional.isPresent()) {
			throw new IllegalStateException(String.format("Customer with id not found:%s", customerId));
		}
		
		// 2. check currency in supported currencies
		boolean isCurrencySupported = ACCEPTED_CURRENCIES.contains(paymentRequest.getPayment().getCurrency());
		if(!isCurrencySupported) {
			throw new IllegalStateException(String.format("Currency is not supported:%s", paymentRequest.getPayment().getCurrency()));
		}
		
		// 3. Charge card
		CardPaymentCharge paymentCharge = cardPaymentCharger.chargeCard(
				paymentRequest.getPayment().getSource(), 
				paymentRequest.getPayment().getAmount(), 
				paymentRequest.getPayment().getCurrency(), 
				paymentRequest.getPayment().getDescription());
		
		// 4. check debited or not
		if(!paymentCharge.isCardDebited()) {
			throw new IllegalStateException(String.format("Card can not debited for customer:%s", customerId));
		}
		
		// 5. Insert payment
		paymentRequest.getPayment().setCustomerId(customerId);
		
		paymentRepository.save(paymentRequest.getPayment());
		
		// 6. send SMS
	}
}
