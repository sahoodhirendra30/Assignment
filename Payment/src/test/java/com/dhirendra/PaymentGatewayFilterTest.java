package com.dhirendra.config;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.mock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class PaymentGatewayFilterTest {

	private ServletRequest request;

	private ServletResponse response;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {

		this.request = new MockHttpServletRequest();
		this.response = new MockHttpServletResponse();
	}

	@Test
	void constructorNullServlet() {
		assertThatIllegalArgumentException().isThrownBy(() -> new MockFilterChain(null));
	}

	@Test
	void constructorNullFilter() {
		assertThatIllegalArgumentException().isThrownBy(() -> new MockFilterChain(mock(Servlet.class)));
	}

	@Test
	void doFilterNullRequest() throws Exception {
		MockFilterChain chain = new MockFilterChain();
		assertThatIllegalArgumentException().isThrownBy(() -> chain.doFilter(null, this.response));
	}

	@Test
	void doFilterNullResponse() throws Exception {
		MockFilterChain chain = new MockFilterChain();
		assertThatIllegalArgumentException().isThrownBy(() -> chain.doFilter(this.request, null));
	}

	@Test
	public void testDoFilter() throws ServletException, IOException {

		PaymentGatewayFilter filter = new PaymentGatewayFilter();

		HttpServletRequest mockReq = Mockito.mock(HttpServletRequest.class);
		HttpServletResponse mockResp = Mockito.mock(HttpServletResponse.class);
		FilterChain mockFilterChain = Mockito.mock(FilterChain.class);
		FilterConfig mockFilterConfig = Mockito.mock(FilterConfig.class);
		Mockito.when(mockReq.getRequestURI()).thenReturn("/");

		BufferedReader br = new BufferedReader(new StringReader("test"));
		Mockito.when(mockReq.getReader()).thenReturn(br);

		filter.init(mockFilterConfig);
		filter.doFilter(mockReq, mockResp, mockFilterChain);
		filter.destroy();
	}

	@Test(expected = Exception.class)
	public void testDoFilterException() throws IOException, ServletException {

		PaymentGatewayFilter filter = new PaymentGatewayFilter();

		HttpServletRequest mockReq = new MockHttpServletRequest();
		HttpServletResponse mockResp = new MockHttpServletResponse();
		FilterChain mockFilterChain = Mockito.mock(FilterChain.class);
		FilterConfig mockFilterConfig = Mockito.mock(FilterConfig.class);

		filter.init(mockFilterConfig);
		filter.doFilter(mockReq, mockResp, mockFilterChain);
		filter.destroy();
	}

}
