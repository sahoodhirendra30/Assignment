package com.dhirendra;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import com.dhirendra.model.PaymentInitiationRequest;
import com.dhirendra.model.PaymentResponse;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@LocalServerPort
	int randomServerPort;

	private static final String BASE_URL = "http://localhost:";

	/**
	 * Payment initiation created return status code 201
	 * 
	 * @throws URISyntaxException
	 */

	@Test
	public void testPaymentInitiationCreated() throws URISyntaxException {
		final String url = BASE_URL + randomServerPort + "/v1.0.0/initiate-payment/";
		URI uri = new URI(url);
		PaymentInitiationRequest paymentInitiationRequest = new PaymentInitiationRequest("NL02RABO7134384551",
				"NL94ABNA1008270121", "1.00", "EUR", "1234");

		HttpHeaders headers = new HttpHeaders();
		headers.set("xRequestId", "29318e25-cebd-498c-888a-f77672f66449");
		headers.set("signatureCertificate", "lox3QmEaxEGU");
		headers.set("signature", "lox3QmEaxEGU");
		HttpEntity<PaymentInitiationRequest> request = new HttpEntity<>(paymentInitiationRequest, headers);

		ResponseEntity<PaymentResponse> result = this.restTemplate.postForEntity(uri, request, PaymentResponse.class);

		// Verify request succeed
		assertThat(result.getStatusCodeValue()).isEqualTo(201);
	}

	@Test
	public void testBadRequest_status_code_400() throws URISyntaxException {
		final String baseUrl = BASE_URL + randomServerPort + "/v1.0.0/initiate-payment/";
		URI uri = new URI(baseUrl);
		PaymentInitiationRequest paymentInitiationRequest = new PaymentInitiationRequest("NL02RABO7134384551",
				"NL94ABNA1008270121", "1.00", "EUR", "1234");

		HttpHeaders headers = new HttpHeaders();
		headers.set("xRequestId", "29318e25-cebd-498c-888a-f77672f66449");
		HttpEntity<PaymentInitiationRequest> request = new HttpEntity<>(paymentInitiationRequest, headers);

		ResponseEntity<PaymentResponse> result = this.restTemplate.postForEntity(uri, request, PaymentResponse.class);

		// Verify request succeed
		assertThat(result.getStatusCodeValue()).isEqualTo(400);
	}
	
	@Test
	public void testBadRequest_status_code_422() throws URISyntaxException {
		final String url = BASE_URL + randomServerPort + "/v1.0.0/initiate-payment/";
		URI uri = new URI(url);
		PaymentInitiationRequest paymentInitiationRequest = new PaymentInitiationRequest("NL02RABO7134384551",
				"NL94ABNA1008270121", "1.00", "EUR", "1234");

		HttpHeaders headers = new HttpHeaders();
		headers.set("xRequestId", "29318e25-cebd-498c-888a-f77672f66449");
		HttpEntity<PaymentInitiationRequest> request = new HttpEntity<>(paymentInitiationRequest, headers);

		ResponseEntity<PaymentResponse> result = this.restTemplate.postForEntity(uri, request, PaymentResponse.class);

		// Verify request succeed
		assertThat(result.getStatusCodeValue()).isEqualTo(422);
	}

	
}
