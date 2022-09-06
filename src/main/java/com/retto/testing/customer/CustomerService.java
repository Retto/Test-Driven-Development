package com.retto.testing.customer;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerService {
	
	private final CustomerRepository customerRepository;
	
	public void registerNewCustomer(CustomerRegistrationRequest request)
	{
		log.info("registerNewCustomer");
	}

}
