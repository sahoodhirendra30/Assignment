package com.dhirendra.exception;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;

import lombok.Data;

@Data
public class PaymentError {

	private HttpStatus status;
	private String message;
	private List<String> errors;

	public PaymentError(HttpStatus status, String message, List<String> errors) {
		super();
		this.status = status;
		this.message = message;
		this.errors = errors;
	}

	public PaymentError(HttpStatus status, String message, String error) {
		super();
		this.status = status;
		this.message = message;
		errors = Arrays.asList(error);
	}

}
