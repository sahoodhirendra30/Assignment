package com.dhirendra.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.dhirendra.exception.PaymentException;
import com.dhirendra.model.PaymentInitiationRequest;
import com.dhirendra.model.PaymentResponse;
import com.dhirendra.model.TransactionStatus;
import com.dhirendra.service.PaymentService;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

	@InjectMocks
	PaymentController paymentController;

	@Mock
	PaymentService paymentService;

	@Test
	public void testPaymentInitiationRequestRequestBodyWithoutHeaders() throws PaymentException {
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
		PaymentInitiationRequest paymentInitiationRequest = new PaymentInitiationRequest("NL02RABO7134384551",
				"NL94ABNA1008270121", "1.00", "EUR", "1234");
		ResponseEntity<PaymentResponse> responseEntity = paymentController.initiatePayment(null, null, null,
				paymentInitiationRequest);
		ResponseEntity.status(HttpStatus.CREATED);
		PaymentResponse paymentResponse = new PaymentResponse();
		paymentResponse.setPaymentId("1");
		paymentResponse.setStatus(TransactionStatus.ACCEPTED);

		when(paymentService.paymentLimitCheck(paymentInitiationRequest)).thenReturn(paymentResponse);
		assertThat(responseEntity.getStatusCodeValue()).isEqualTo(201);
		assertThat(paymentResponse.getStatus()).isEqualTo("Accepted");
		assertFalse(paymentResponse.getPaymentId().isEmpty());
	}

	@SuppressWarnings("unlikely-arg-type")
	@Test
	public void testContentTypeAndWithPaymentIdHeader() throws PaymentException {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setContentType(MediaType.TEXT_PLAIN_VALUE);
		request.addHeader("xRequestId", "29318e25-cebd-498c-888a-f77672f66449");
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
		PaymentInitiationRequest paymentInitiationRequest = new PaymentInitiationRequest("NL02RABO7134384551",
				"NL94ABNA1008270121", "1.00", "EUR", "1234");
		ResponseEntity<PaymentResponse> responseEntity = paymentController.initiatePayment(null, null, null,
				paymentInitiationRequest);
		ResponseEntity.status(HttpStatus.CREATED);
		PaymentResponse paymentResponse = new PaymentResponse();
		paymentResponse.setPaymentId("1");
		paymentResponse.setStatus(TransactionStatus.ACCEPTED);

		when(paymentService.paymentLimitCheck(paymentInitiationRequest)).thenReturn(paymentResponse);
		assertThat(responseEntity.getHeaders().get("xRequestId").equals("29318e25-cebd-498c-888a-f77672f66449"));
		assertThat(request.getContentType().equalsIgnoreCase("text/plain"));
		
	}

}
