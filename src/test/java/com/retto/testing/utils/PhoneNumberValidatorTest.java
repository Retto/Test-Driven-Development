package com.retto.testing.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class PhoneNumberValidatorTest {

	private PhoneNumberValidator underTest;
	
	@BeforeEach
	void setUp() {
		underTest = new PhoneNumberValidator();
	}
	
	@ParameterizedTest
	@CsvSource({
		"+170000000000,true",
        "+17000000565000, false",
        "17000000000, false"
	})
	void itShouldValidatePhoneNumber(String phoneNumber, boolean exptected) {
		// When
		boolean isValid = underTest.test(phoneNumber);
		
		// Then
		assertThat(isValid).isEqualTo(exptected);
	}
}
