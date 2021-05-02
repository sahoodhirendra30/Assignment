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
	
	private MockMvc mockMvc;

	@InjectMocks
	PaymentController paymentController;

	@Mock
	PaymentService paymentService;
	
	@BeforeEach
	public void setup() {
		JacksonTester.initFields(this, new ObjectMapper());
		
		// MockMvc standalone approach
		mockMvc = MockMvcBuilders.standaloneSetup(paymentController).setControllerAdvice(new PaymentExceptionHandler())
				.build();
	}
	
	/**
	 * payment limit check exception
	 * @throws Exception
	 */

	@Test
	public void testlimitCheck() throws Exception {
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		mockRequest.addHeader("xRequestId", "29318e25-cebd-498c-888a-f77672f66449");
		mockRequest.addHeader("signatureCertificate", "lox3QmEaxEGU");
		mockRequest.addHeader("signature", "lox3QmEaxEGU");		
		mockRequest.setContentType(MediaType.APPLICATION_JSON_VALUE);
		
		PaymentInitiationRequest paymentInitiationRequest = new PaymentInitiationRequest("NL02RABO7134384551",
				"NL94ABNA1008270121", "1.00", "EUR", "1234");
		
		
		// given
		given(paymentService.paymentLimitCheck(paymentInitiationRequest)).willReturn(
				new PaymentResponse("1001", TransactionStatus.REJECTED, "Fail", ErrorReasonCode.LIMIT_EXCEEDED));

		// when
		MockHttpServletResponse response = mockMvc
				.perform(post("/v0.1.1/initiate-payment").accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

		// then
		assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		assertThat(response.getContentAsString()).isEqualTo(paymentResponse
				.write(new PaymentResponse("1001", TransactionStatus.REJECTED, "Fail", ErrorReasonCode.LIMIT_EXCEEDED))
				.getJson());
	}
	
	@Test
	public void testNotSetHeader() throws Exception {

		PaymentInitiationRequest paymentInitiationRequest = new PaymentInitiationRequest("NL02RABO7134384551",
				"NL94ABNA1008270121", "1.00", "EUR", "1234");
		// given
		given(paymentService.paymentLimitCheck(paymentInitiationRequest).willThrow(new PaymentException(0, null)));

		// when
		MockHttpServletResponse response = mockMvc.perform(post("/initiate-payment").accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();

		// then
		assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
		assertThat(response.getContentAsString()).isEmpty();
	}
	

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
