package com.dhirendra.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TransactionStatus {

	ACCEPTED("Accepted"), REJECTED("Rejected");

	private String value;

	TransactionStatus(String value) {
		this.value = value;
	}

	@Override
	@JsonValue
	public String toString() {
		return String.valueOf(value);
	}

	@JsonCreator
	public static TransactionStatus fromValue(String text) {
		for (TransactionStatus b : TransactionStatus.values()) {
			if (String.valueOf(b.value).equals(text)) {
				return b;
			}
		}
		return null;
	}

}
