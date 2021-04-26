package com.dhirendra.config;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.springframework.http.MediaType;

import com.dhirendra.model.PaymentInitiationRequest;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * Request wrapper class
 * 
 * @author
 *
 */
@Slf4j
public class HttpServletRequestWriter extends HttpServletRequestWrapper {

	HttpServletRequest req = null;

	public HttpServletRequestWriter(HttpServletRequest request) {
		super(request);
		this.req = request;
	}

	public HttpServletRequestWriter(HttpServletRequest request, PaymentInitiationRequest paymentInitiationRequest) {
		super(request);
		this.req = request;
	}

	public void addHeader(String name, String uuid) {
		setHeader(name);
	}

	/**
	 * set header type
	 * 
	 * @param headerName
	 */

	public void setHeader(String headerName) {
		String headerValue = super.getHeader(headerName);
		if ("Accept".equalsIgnoreCase(headerName)) {
			headerValue.replaceAll(MediaType.TEXT_PLAIN_VALUE, MediaType.APPLICATION_JSON_VALUE);
		} else if ("Content-Type".equalsIgnoreCase(headerName)) {
			headerValue.replaceAll(MediaType.TEXT_PLAIN_VALUE, MediaType.APPLICATION_JSON_VALUE);
		}

	}

	@Override
	public Enumeration<String> getHeaders(String name) {
		List<String> headerVals = Collections.list(super.getHeaders(name));
		int index = 0;
		for (String value : headerVals) {
			if ("Content-Type".equalsIgnoreCase(name)) {
				log.debug("Content type change: ");
				headerVals.set(index, MediaType.APPLICATION_JSON_VALUE);
			}
			index++;
		}
		return Collections.enumeration(headerVals);
	}

	/**
	 * header information
	 * @param request
	 * @return
	 */
	public Map<String, String> getHeadersInfo(HttpServletRequest request) {
		Map<String, String> map = new HashMap<String, String>();

		Enumeration headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String key = (String) headerNames.nextElement();
			String value = request.getHeader(key);
			map.put(key, value);
		}

		return map;
	}

	@Override
	public String getContentType() {
		String contentTypeValue = super.getContentType();
		if (MediaType.TEXT_PLAIN_VALUE.equalsIgnoreCase(contentTypeValue)) {
			return MediaType.APPLICATION_JSON_VALUE;
		}
		return contentTypeValue;
	}

}
