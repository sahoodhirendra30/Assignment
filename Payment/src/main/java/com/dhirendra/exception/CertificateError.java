package com.dhirendra.exception;

import com.dhirendra.model.ErrorReasonCode;

public class CertificateError extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CertificateError(String errorMessage, Throwable err) {
		super(errorMessage, err);
	}

	public CertificateError(String errorMessage, ErrorReasonCode code) {
		super(errorMessage);
	}

	public CertificateError(String errorMessage, Throwable err, ErrorReasonCode code) {
		super(errorMessage, err);
	}

	public CertificateError(String errorMessage, ErrorReasonCode code, ErrorReasonCode codeValue) {
		super(errorMessage);
	}
}