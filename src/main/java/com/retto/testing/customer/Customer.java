package com.retto.testing.customer;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
//@JsonIgnoreProperties(value = {"id"}, allowGetters = true)
@JsonIgnoreProperties(allowGetters = true)
public class Customer {

	@Id
	private UUID id;
	
	@NotBlank
	@Column(nullable = false)
	private String name;
	
	@NotBlank
	@Column(nullable=false, unique = true)
	private String phoneNumber;
	
	public Customer(UUID id, String name, String phoneNumber) {
		this.id = id;
		this.name = name;
		this.phoneNumber = phoneNumber;
	}
}
