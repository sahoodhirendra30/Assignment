package com.dhirendra.controller;

import java.security.cert.CertificateException;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dhirendra.exception.PaymentException;
import com.dhirendra.model.PaymentInitiationRequest;
import com.dhirendra.model.PaymentResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Validated
public interface BaseController {

	@Operation(summary = "Initiate a payment", description = "This API receives a payment initiation request. The API validates the request and returns transaction status with signature that the client can verify the response.", tags = {})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "The 201 response of the payment initiation endpoint", content = @Content(schema = @Schema(implementation = PaymentResponse.class))),

			@ApiResponse(responseCode = "400", description = "The 400 response of the payment initiation endpoint", content = @Content(schema = @Schema(implementation = PaymentResponse.class))),

			@ApiResponse(responseCode = "422", description = "The 422 response of the payment initiation endpoint", content = @Content(schema = @Schema(implementation = PaymentResponse.class))),

			@ApiResponse(responseCode = "500", description = "The 500 response of the payment initiation endpoint", content = @Content(schema = @Schema(implementation = PaymentResponse.class))) })
	@RequestMapping(value = "/v1.0.0/initiate-payment", produces = { "application/json" }, consumes = {
			"application/json" }, method = RequestMethod.POST)
	ResponseEntity<PaymentResponse> initiatePayment(
			@Parameter(in = ParameterIn.HEADER, description = "", required = true, schema = @Schema()) @RequestHeader(value = "X-Request-Id", required = true) String xRequestId,
			@Parameter(in = ParameterIn.HEADER, description = "", required = true, schema = @Schema()) @RequestHeader(value = "Signature-Certificate", required = true) String signatureCertificate,
			@Parameter(in = ParameterIn.HEADER, description = "", required = true, schema = @Schema()) @RequestHeader(value = "Signature", required = true) String signature,
			@Parameter(in = ParameterIn.DEFAULT, description = "The payment initiation request body", required = true, schema = @Schema()) @Valid @RequestBody PaymentInitiationRequest body)
			throws PaymentException, CertificateException;

}
