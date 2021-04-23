package com.dhirendra.controller;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.dhirendra.exception.CertificateError;
import com.dhirendra.model.ErrorReasonCode;
import com.dhirendra.model.PaymentInitiationRequest;
import com.dhirendra.model.PaymentResponse;
import com.dhirendra.model.TransactionStatus;
import com.dhirendra.util.PaymentUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/v1.0.0")
public class PaymentController implements BaseController {

	private final HttpServletRequest request;

	public PaymentController(ObjectMapper objectMapper, HttpServletRequest request) {
		this.request = request;
	}

	@PostMapping(value = "/initiate-payment")
	public ResponseEntity<PaymentResponse> initiatePayment(
			@RequestHeader(value = "X-Request-Id", required = true) String xRequestId,
			@RequestHeader(value = "Signature-Certificate", required = true) String signatureCertificate,
			@RequestHeader(value = "Signature", required = true) String signature,
			@Valid @RequestBody PaymentInitiationRequest paymentInitiationRequest) throws CertificateError {

		try {

			if (request.getHeader("Accept") != null && request.getHeader("Accept").contains("application/json")) {
				try {

					Double amount = Double.parseDouble(paymentInitiationRequest.getAmount());

					if ((amount > 0) && (PaymentUtil.sum(paymentInitiationRequest.getDebtorIBAN()))
							% ((paymentInitiationRequest.getDebtorIBAN().length())) == 0) {
						
						PaymentResponse paymentResponse = new PaymentResponse();
						paymentResponse.setStatus(TransactionStatus.REJECTED);
						paymentResponse.setReasonCode(ErrorReasonCode.LIMIT_EXCEEDED);
						return new ResponseEntity<PaymentResponse>(paymentResponse, HttpStatus.BAD_REQUEST);

					} else {

						PaymentResponse paymentResponse = new PaymentResponse();
						paymentResponse.setPaymentId(UUID.randomUUID().toString());
						paymentResponse.setStatus(TransactionStatus.ACCEPTED);
						return new ResponseEntity<PaymentResponse>(paymentResponse, HttpStatus.CREATED);
					}

				} catch (RequestRejectedException e) {
					log.error("Request rejected", e);
					PaymentResponse paymentResponse = new PaymentResponse();
					paymentResponse.setStatus(TransactionStatus.REJECTED);
					paymentResponse.setReason(e.getMessage());
					paymentResponse.setReasonCode(ErrorReasonCode.INVALID_REQUEST);
					return new ResponseEntity<PaymentResponse>(paymentResponse, HttpStatus.BAD_REQUEST);
				}
				
				catch (MethodArgumentTypeMismatchException e) {
					log.error("Request rejected", e);
					PaymentResponse paymentResponse = new PaymentResponse();
					paymentResponse.setStatus(TransactionStatus.REJECTED);
					paymentResponse.setReason(e.getMessage());
					paymentResponse.setReasonCode(ErrorReasonCode.INVALID_REQUEST);
					return new ResponseEntity<PaymentResponse>(paymentResponse, HttpStatus.UNPROCESSABLE_ENTITY);
				}

				catch (Exception e) {
					log.error("Couldn't serialize response for content type application/json", e);
					PaymentResponse paymentResponse = new PaymentResponse();
					paymentResponse.setStatus(TransactionStatus.REJECTED);
					paymentResponse.setReason(e.getMessage());
					paymentResponse.setReasonCode(ErrorReasonCode.INVALID_REQUEST);
					return new ResponseEntity<PaymentResponse>(HttpStatus.INTERNAL_SERVER_ERROR);
				}

			}

			return new ResponseEntity<PaymentResponse>(HttpStatus.NOT_IMPLEMENTED);

		} catch (Exception e) {

			throw new CertificateError("singnature not valid", ErrorReasonCode.INVALID_SIGNATURE,
					ErrorReasonCode.fromValue(signature));
		}
	}
}
