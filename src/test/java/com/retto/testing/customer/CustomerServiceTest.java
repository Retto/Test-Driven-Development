package com.retto.testing.customer;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.retto.testing.utils.PhoneNumberValidator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

public class CustomerServiceTest {
	
  @Mock  	
  private CustomerRepository customerRepository;
  
  @Mock  	
  private PhoneNumberValidator phoneNumberValidator;
  
  @Captor
  private ArgumentCaptor<Customer> customerArgumentCaptor;
	
  private CustomerService underTest;
  
  @BeforeEach
  void setUp() {
	  MockitoAnnotations.openMocks(this);
	  underTest = new CustomerService(customerRepository, phoneNumberValidator);
  }
  
  @Test
  void itShouldSaveNewCustomer() {
	  // Given a phone number 
	  String phoneNumber = "1234";
	  Customer customer = new Customer(UUID.randomUUID(), "Retto", phoneNumber);
	  
	  // init a request
	  CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);
	  
	  // no customer with phone number passed
	  given(customerRepository.findByPhoneNumber(phoneNumber)).willReturn(Optional.empty());
	  
	  given(phoneNumberValidator.test(phoneNumber)).willReturn(true);
	    
	  // When
	  underTest.registerNewCustomer(request);
	    
	  // Then
	  then(customerRepository).should().save(customerArgumentCaptor.capture());
	  Customer captorCustomer = customerArgumentCaptor.getValue();
	  assertThat(captorCustomer).isEqualTo(customer);
  }
  
  @Test
  void itShouldNotSaveNewCustomerWhenPhoneNumberIsInvalid() {
	  // Given a phone number
      String phoneNumber = "1234";
      Customer customer = new Customer(UUID.randomUUID(), "Retto", phoneNumber);

      // init a request
      CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);


      // non-valid phone number
      given(phoneNumberValidator.test(phoneNumber)).willReturn(false);

      // When
      assertThatThrownBy(() -> underTest.registerNewCustomer(request))
              .isInstanceOf(IllegalStateException.class)
              .hasMessageContaining(String.format("Phone number is not valid:%s", phoneNumber));

      // Then
      then(customerRepository).shouldHaveNoInteractions();
  }
  
  @Test
  void itShouldSaveNewCustomerWhenIdIsNull() {
	  // Given a phone number
	  String phoneNumber = "1234";
	  Customer customer = new Customer(null, "Retto", phoneNumber);
	  
	  // init a request
	  CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);
	  
	  // no customer with phone number passed
	  given(customerRepository.findByPhoneNumber(phoneNumber)).willReturn(Optional.empty());
	  
	  given(phoneNumberValidator.test(phoneNumber)).willReturn(true);
	  
	  // When
	  underTest.registerNewCustomer(request);
	  
	  // Then
	  then(customerRepository).should().save(customerArgumentCaptor.capture());
	  Customer captorCustomer = customerArgumentCaptor.getValue();
	  assertThat(captorCustomer).usingRecursiveComparison().ignoringFields("id").isEqualTo(customer);
	  assertThat(captorCustomer.getId()).isNotNull();
  }

  @Test
  void itShouldNotSaveCustomerWhenCustomerExists() {
      // Given a phone number
      String phoneNumber = "1234";
      Customer customer = new Customer(UUID.randomUUID(), "Retto", phoneNumber);

      // init a request
      CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

      // check existing customer is returned
      given(customerRepository.findByPhoneNumber(phoneNumber))
              .willReturn(Optional.of(customer));
      
      given(phoneNumberValidator.test(phoneNumber)).willReturn(true);
      
      // When
      underTest.registerNewCustomer(request);

      // Then
      then(customerRepository).should(never()).save(any());
  }

  @Test
  void itShouldThrowWhenPhoneNumberIsTaken() {
      // Given a phone number
      String phoneNumber = "1234";
      Customer customer = new Customer(UUID.randomUUID(), "Retto", phoneNumber);
      Customer customerTwo = new Customer(UUID.randomUUID(), "Retto2", phoneNumber);

      // init a request
      CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

      // no customer with phone number passed
      given(customerRepository.findByPhoneNumber(phoneNumber))
              .willReturn(Optional.of(customerTwo));

      given(phoneNumberValidator.test(phoneNumber)).willReturn(true);
      // When
      
      // Then
      assertThatThrownBy(() -> underTest.registerNewCustomer(request))
              .isInstanceOf(IllegalStateException.class)
              .hasMessageContaining(String.format("Phone number is taken:%s", phoneNumber));

      // Finally
      then(customerRepository).should(never()).save(any(Customer.class));

  }
}
