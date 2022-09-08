package com.retto.testing.payment;

import java.math.BigDecimal;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Data
@AllArgsConstructor
public class Payment {

	@Id
	@GeneratedValue
	private Long paymentId;

	private UUID customerId;

	private BigDecimal amount;

	private Currency currency;

	private String source;

	private String description;
}
