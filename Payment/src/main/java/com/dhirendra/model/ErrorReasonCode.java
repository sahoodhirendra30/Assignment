package com.dhirendra.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ErrorReasonCode {

	UNKNOWN_CERTIFICATE("UNKNOWN_CERTIFICATE"), INVALID_SIGNATURE("INVALID_SIGNATURE"),
	INVALID_REQUEST("INVALID_REQUEST"), LIMIT_EXCEEDED("LIMIT_EXCEEDED"), GENERAL_ERROR("GENERAL_ERROR");

	private String value;

	ErrorReasonCode(String value) {
		this.value = value;
	}

	@Override
	@JsonValue
	public String toString() {
		return String.valueOf(value);
	}

	@JsonCreator
	public static ErrorReasonCode fromValue(String text) {
		for (ErrorReasonCode b : ErrorReasonCode.values()) {
			if (String.valueOf(b.value).equals(text)) {
				return b;
			}
		}
		return null;
	}
}
