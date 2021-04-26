package com.dhirendra.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

	private final HttpServletRequest request;

	@Autowired
	PaymentService paymentService;

	public PaymentController(HttpServletRequest request) {
		this.request = request;
	}

	@PostMapping(value = "/initiate-payment")
	public ResponseEntity<PaymentResponse> initiatePayment(
			@RequestHeader(value = "X-Request-Id", required = true) String xRequestId,
			@RequestHeader(value = "Signature-Certificate", required = true) String signatureCertificate,
			@RequestHeader(value = "Signature", required = true) String signature,
			@Valid @RequestBody PaymentInitiationRequest paymentInitiationRequest) throws PaymentException {

		if (request.getHeader("Accept") != null && request.getHeader("Accept").contains("application/json")) {

			PaymentResponse paymentResponse = paymentService.paymentLimitCheck(paymentInitiationRequest);
			return new ResponseEntity<PaymentResponse>(paymentResponse, HttpStatus.CREATED);
		}

		log.error("content type is not application/json", paymentInitiationRequest);
		return new ResponseEntity<PaymentResponse>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
