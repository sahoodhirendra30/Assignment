package com.dhirendra;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

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
	
	@Autowired
	PaymentService paymentService;

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
	
	@Test
	public void testPaymentResponse() throws URISyntaxException, JsonProcessingException {

		Resource requestBody = new ClassPathResource("input.json", this.getClass());
		Resource responseBody = new ClassPathResource("output.json", this.getClass());

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setInterceptors(Collections.singletonList(new ContentInterceptor(requestBody)));
		MockRestServiceServer mockServer = MockRestServiceServer.bindTo(restTemplate).build();

		HttpHeaders headers = new HttpHeaders();

		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("xRequestId", "29318e25-cebd-498c-888a-f77672f66449");
		map.add("signatureCertificate", "lox3QmEaxEGU");
		map.add("signature", "lox3QmEaxEGU");
		headers.addAll(map);

		mockServer.expect(requestTo(new URI("/v1.0.0/initiate-payment")))
				.andExpect(method(HttpMethod.POST))
				.andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON).headers(headers));
		
		restTemplate.postForObject("/v1.0.0/initiate-payment", requestBody, PaymentResponse.class);

	}

	private static class ContentInterceptor implements ClientHttpRequestInterceptor {

		private final Resource resource;

		private ContentInterceptor(Resource resource) {
			this.resource = resource;
		}

		@Override
		public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
				throws IOException {
			ClientHttpResponse response = execution.execute(request, body);
			byte[] expected = FileCopyUtils.copyToByteArray(this.resource.getInputStream());
			byte[] actual = FileCopyUtils.copyToByteArray(response.getBody());
			assertEquals(new String(expected), new String(actual));
			return response;

		}
	}

	
}
