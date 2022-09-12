package com.retto.testing.customer;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.retto.testing.utils.PhoneNumberValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerService {
	
	private final CustomerRepository customerRepository;
	private final PhoneNumberValidator phoneNumberValidator;
	
	
	public void registerNewCustomer(CustomerRegistrationRequest request)
	{
		log.info("registerNewCustomer");
		
		String phoneNumber = request.getCustomer().getPhoneNumber();
		
		if(!phoneNumberValidator.test(phoneNumber)) {
			throw new IllegalStateException(String.format("Phone number is not valid:%s", phoneNumber));
		}
		
		Optional<Customer> customerOptional = customerRepository.findByPhoneNumber(phoneNumber);
		if(customerOptional.isPresent())
		{
			Customer customer = customerOptional.get();
			if(customer.getName().equals(request.getCustomer().getName())) {
				return;
			}
			
			throw new IllegalStateException(String.format("Phone number is taken:%s", phoneNumber));
		}
		
		if(request.getCustomer().getId() == null)
		{
			request.getCustomer().setId(UUID.randomUUID());
		}
		
		customerRepository.save(request.getCustomer());
	}

}
