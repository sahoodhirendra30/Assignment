package com.dhirendra.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dhirendra.exception.PaymentException;
import com.dhirendra.model.PaymentInitiationRequest;
import com.dhirendra.model.PaymentResponse;
import com.dhirendra.service.PaymentService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/v1.0.0")
public class PaymentController implements BaseController {

	@Autowired
	PaymentService paymentService;

	/**
	 * 
	 * POST method to initiate the payment
	 */
	
	@PostMapping(value = "/initiate-payment", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<PaymentResponse> initiatePayment(
			@RequestHeader(value = "X-Request-Id", required = true) String xRequestId,
			@RequestHeader(value = "Signature-Certificate", required = true) String signatureCertificate,
			@RequestHeader(value = "Signature", required = true) String signature,
			@Valid @RequestBody PaymentInitiationRequest paymentInitiationRequest) throws PaymentException {

		log.info("Inside paymentController controller initiatePayment()");
		PaymentResponse paymentResponse = paymentService.paymentLimitCheck(paymentInitiationRequest);
		return new ResponseEntity<PaymentResponse>(paymentResponse, HttpStatus.CREATED);
	}

}
