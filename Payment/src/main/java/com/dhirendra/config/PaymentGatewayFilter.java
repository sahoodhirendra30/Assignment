package com.dhirendra.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Enumeration;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.dhirendra.exception.CertificateError;
import com.dhirendra.exception.PaymentException;
import com.dhirendra.model.ErrorReasonCode;
import com.dhirendra.model.PaymentInitiationRequest;
import com.dhirendra.util.PaymentCertUtil;
import com.dhirendra.util.PaymentUtil;
import com.google.gson.Gson;

import static com.dhirendra.util.PaymentCertUtil.decrypt;
import static com.dhirendra.util.PaymentCertUtil.stringToPrivateKeyConverter;
import static com.dhirendra.util.PaymentCertUtil.stringToPublicKeyConverter;
import static com.dhirendra.util.PaymentCertUtil.verify;
import static com.dhirendra.util.PaymentUtil.getRequestBody;

import lombok.extern.slf4j.Slf4j;

/**
 * Filter class to check the CN name before allowing the request to reach to REST end point.
 * 
 * @author 
 *
 */
@Slf4j
@Component
@WebFilter(displayName = "EncryptionFilter", urlPatterns = "/*")
public class PaymentGatewayFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		log.info("servlet filter wrap the original encrypted request and response");

		if (response.getCharacterEncoding() == null) {
			response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
		}

		HttpServletRequest originalRequest = (HttpServletRequest) request;
		HttpServletResponse originalResponse = (HttpServletResponse) response;

		try {
			boolean allowAccess = checkCN(originalRequest, originalResponse);

			if (allowAccess) {

				// verify incoming signature with cerificate

				String signature = originalRequest.getHeader("signature");
				String signatureCertificate = originalRequest.getHeader("signatureCertificate");
				// String json encrypted
				String requestBody = getRequestBody((HttpServletRequest) request);
				if (verify(requestBody, signature, stringToPublicKeyConverter(signatureCertificate))) {

					String privateKey = "";

					// String json decrypted
					String decryptedBody = decrypt(requestBody, stringToPrivateKeyConverter(privateKey));
					String decryptedHeader = decrypt(originalRequest.getHeader("xRequestId").toString(),
							stringToPrivateKeyConverter(privateKey));

					PaymentInitiationRequest paymentInitiationRequest = (PaymentInitiationRequest) PaymentUtil
							.getModelObject(decryptedBody);
					HttpServletRequestWriter httpServletRequestWritableWrapper = new HttpServletRequestWriter(
							originalRequest, paymentInitiationRequest);
					httpServletRequestWritableWrapper.addHeader("xRequestId", decryptedHeader);
					httpServletRequestWritableWrapper.addHeader("signatureCertificate", signatureCertificate);
					httpServletRequestWritableWrapper.addHeader("signature", signature);

					HttpServletResponseWriter httpServletResponseWriter = new HttpServletResponseWriter(
							originalResponse);

					chain.doFilter(httpServletRequestWritableWrapper, httpServletResponseWriter);

					String encryptedResponse = PaymentCertUtil.signSHA256RSA(httpServletResponseWriter.getResponse(),
							privateKey);
				}
			} else {

				throw new CertificateError("The signature is not a valid one", ErrorReasonCode.UNKNOWN_CERTIFICATE);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public boolean checkCN(HttpServletRequest request, HttpServletResponse response) throws PaymentException {

		boolean isAllowed;		

		X509Certificate certs[] = (X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");
		if (null != PaymentCertUtil.getCN(certs) && "".equalsIgnoreCase(PaymentCertUtil.getCN(certs))) {
				
			isAllowed = true;
						
		} else {
			isAllowed = false;			
		}
		return isAllowed;
	}

	private void writeResponse(ServletResponse response, String responseString) throws IOException {
		PrintWriter out = response.getWriter();
		out.print(responseString);
		out.flush();
		out.close();
	}
}