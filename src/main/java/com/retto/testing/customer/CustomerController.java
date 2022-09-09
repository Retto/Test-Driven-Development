package com.retto.testing.customer;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("api/v1/customer")
@RequiredArgsConstructor
@Slf4j
public class CustomerController {
	
	private final CustomerService customerService;

	@PutMapping
	public void registerNewCustomer(@RequestBody CustomerRegistrationRequest request)
	{
		log.info("registerNewCustomer");
		customerService.registerNewCustomer(request);
	}
}
