package com.dhirendra.model;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.Assert.assertThat;

class PaymentInitiationRequestTest {

	private Validator validator;

	@BeforeEach
	public void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	Set<ConstraintViolation<PaymentInitiationRequest>> constraintViolations;

	@Test
	public void testPaymentInitiationRequestSuccess() {
		PaymentInitiationRequest paymentInitiationRequest = new PaymentInitiationRequest();
		paymentInitiationRequest.setDebtorIBAN("NL02RABO7134384551");
		paymentInitiationRequest.setCreditorIBAN("NL94ABNA1008270121");
		paymentInitiationRequest.setAmount("1.00");
		paymentInitiationRequest.setCurrency("EUR");
		paymentInitiationRequest.setEndToEndId(null);

		Set<ConstraintViolation<PaymentInitiationRequest>> violations = validator.validate(paymentInitiationRequest);
		assertTrue(violations.isEmpty());
	}

	@Test
	public void shouldPassLengthValidationPropertyInputValuesFor_DebtorIBAN() {
		String inputValue = "IBAN2";
		constraintViolations = validator.validateValue(PaymentInitiationRequest.class, "debtorIBAN", inputValue);
		assertThat("Should have got a validation error - input: " + inputValue, constraintViolations.size(), is(1));
	}

	@Test
	public void shouldPassLengthValidationPropertyInputValuesFor_Creditor_IBAN() {
		String inputValue = "IBAN1";
		constraintViolations = validator.validateValue(PaymentInitiationRequest.class, "debtorIBAN", inputValue);
		assertThat("Should have got a validation error - input: " + inputValue, constraintViolations.size(), is(1));
	}

}
