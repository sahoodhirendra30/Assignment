package com.dhirendra.config;

import static com.dhirendra.util.PaymentCertUtil.decrypt;
import static com.dhirendra.util.PaymentCertUtil.stringToPrivateKeyConverter;
import static com.dhirendra.util.PaymentCertUtil.stringToPublicKeyConverter;
import static com.dhirendra.util.PaymentCertUtil.verify;
import static com.dhirendra.util.PaymentUtil.getRequestBody;
import static com.dhirendra.util.PaymentCertUtil.encrypt;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.dhirendra.exception.CertificateError;
import com.dhirendra.exception.PaymentException;
import com.dhirendra.model.ErrorReasonCode;
import com.dhirendra.model.PaymentInitiationRequest;
import com.dhirendra.util.PaymentCertUtil;
import com.dhirendra.util.PaymentUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * Filter class to check the CN name before allowing the request to reach to
 * REST end point.
 * 
 * @author
 *
 */
@Slf4j
@Component
@WebFilter(displayName = "filter", urlPatterns = "/*")
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
					String publicKey = "";

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

					
					String encryptedHeader = encrypt(httpServletResponseWriter.getHeader("xRequestId").toString(),
							stringToPublicKeyConverter(publicKey));
					
					String encryptResponseBody = encrypt(PaymentUtil.getServletResponseAsString(httpServletResponseWriter.getResponse()),
							stringToPublicKeyConverter(publicKey));
					
					//httpServletResponseWriter.addHeader("xRequestId", encryptedHeader);
					//httpServletResponseWriter.addHeader("signature", signature);
					Map<String, String> headers = new HashMap();
					headers.put("xRequestId", encryptedHeader);
					headers.put("signature", signature);
					httpServletResponseWriter.setContentType(MediaType.TEXT_PLAIN_VALUE);

					String encryptedResponse = PaymentCertUtil.signSHA256WithRSA(encryptResponseBody,
							headers, privateKey);
					httpServletResponseWriter.setResponse(encryptedResponse);
					
					chain.doFilter(httpServletRequestWritableWrapper, httpServletResponseWriter);
					
				}
			} else {

				throw new CertificateError("The signature is not a valid one", ErrorReasonCode.INVALID_SIGNATURE);

			}
		} catch (Exception e) {
			throw new CertificateError("The certificate not valid", ErrorReasonCode.UNKNOWN_CERTIFICATE);
			
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

}