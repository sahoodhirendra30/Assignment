package com.dhirendra.controller;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.dhirendra.exception.PaymentException;
import com.dhirendra.model.PaymentInitiationRequest;
import com.dhirendra.model.PaymentResponse;
import com.dhirendra.service.PaymentService;

import lombok.extern.slf4j.Slf4j;

/**
 * This class intends to initiate the payment
 * 
 * @author
 *
 */
@RestController
@RequestMapping("/v0.1.1")
@Slf4j
public class TestController {

	@Autowired
	PaymentService paymentService;

	/**
	 * 
	 * @param xRequestId
	 * @param signatureCertificate
	 * @param signature
	 * @param paymentInitiationRequest
	 * @return
	 * @throws PaymentException
	 */
	@PostMapping(value = "/initiate-payment", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PaymentResponse> initiatePayment(
			@RequestHeader(value = "X-Request-Id", required = true) String xRequestId,
			@RequestHeader(value = "Signature-Certificate", required = true) String signatureCertificate,
			@RequestHeader(value = "Signature", required = true) String signature,
			@Valid @RequestBody PaymentInitiationRequest paymentInitiationRequest) throws PaymentException {

		log.info("Inside paymentController controller initiatePayment()");
		PaymentResponse paymentResponse = paymentService.paymentLimitCheck(paymentInitiationRequest);
		HttpHeaders responseHeaders = new HttpHeaders();

		responseHeaders.addAll(createResponseHeader(xRequestId, signatureCertificate, signature));
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/").buildAndExpand(paymentResponse)
				.toUri();
		return ResponseEntity.created(location).headers(responseHeaders).body(paymentResponse);
	}

	private MultiValueMap<String, String> createResponseHeader(String xRequestId, String signatureCertificate,
			String signature) {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("xRequestId", xRequestId);
		map.add("signatureCertificate", signatureCertificate);
		map.add("signature", signature);

		return map;
	}

}
