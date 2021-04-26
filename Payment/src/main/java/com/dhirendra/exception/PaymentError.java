package com.dhirendra.exception;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;

import com.dhirendra.model.ErrorReasonCode;

import lombok.Data;

@Data
public class PaymentError {

	private HttpStatus status;
	private String message;
	private List<String> errors;
	private ErrorReasonCode errorReasonCode;

	public PaymentError(HttpStatus status, String message, List<String> errors, ErrorReasonCode errorReasonCode) {
		super();
		this.status = status;
		this.message = message;
		this.errors = errors;
		this.errorReasonCode = errorReasonCode;
	}

	public PaymentError(HttpStatus status, String message, String error, ErrorReasonCode errorReasonCode) {
		super();
		this.status = status;
		this.message = message;
		errors = Arrays.asList(error);
		this.errorReasonCode = errorReasonCode;
	}

}
