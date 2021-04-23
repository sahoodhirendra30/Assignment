package com.dhirendra.exception;

public class PaymentException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int code;

	public PaymentException(int code, String msg) {
		super(msg);
		this.code = code;
	}
}
