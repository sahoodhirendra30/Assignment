package com.dhirendra.controller;

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.dhirendra.model.PaymentInitiationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest
class PaymentControllerTest {

	@Autowired
	private MockMvc mvc;

	@Test
	public void prepareRequestBody() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/v1.0.0/initiate-payment")
				.content(asJsonString(new PaymentInitiationRequest("NL02RABO7134384551", "NL94ABNA1008270121", "1.00",
						"EUR", "1234")))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))

				.andExpect(status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("$.endToEndId").exists());
	}

	public static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	void testCreateResponseObjectHttpStatus() {
		fail("Not yet implemented");
	}

	@Test
	void testCreateResponseHttpStatus() {
		fail("Not yet implemented");
	}

	@Test
	void testCreateResponseHttpHeadersHttpStatus() {
		fail("Not yet implemented");
	}

}
