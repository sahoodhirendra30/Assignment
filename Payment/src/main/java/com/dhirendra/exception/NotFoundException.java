package com.dhirendra.exception;

public class NotFoundException extends PaymentException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int code;

	public NotFoundException(int code, String msg) {
		super(code, msg);
		this.code = code;
	}

}
