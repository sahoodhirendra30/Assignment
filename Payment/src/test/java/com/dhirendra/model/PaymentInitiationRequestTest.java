package com.dhirendra.model;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PaymentInitiationRequestTest {

	private Validator validator;

	@BeforeEach
	public void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
	public void testPaymentInitiationRequestSuccess() {
		PaymentInitiationRequest paymentInitiationRequest = new PaymentInitiationRequest();
		paymentInitiationRequest.setDebtorIBAN("NL02RABO7134384551");
		paymentInitiationRequest.setCreditorIBAN("NL94ABNA1008270121");
		paymentInitiationRequest.setAmount("1.00");
		paymentInitiationRequest.setCurrency("EUR");
		paymentInitiationRequest.setEndToEndId(null);

		Set<ConstraintViolation<PaymentInitiationRequest>> violations = validator.validate(paymentInitiationRequest);
		assertFalse(violations.isEmpty());
	}
	


}
