package com.dhirendra.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Validated
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class PaymentResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonProperty("paymentId")
	private String paymentId = null;

	@JsonProperty("status")
	private TransactionStatus status = null;

	@JsonProperty("reason")
	private String reason = null;

	@JsonProperty("reasonCode")
	private ErrorReasonCode reasonCode = null;

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		PaymentResponse paymentRejectedResponse = (PaymentResponse) o;
		return Objects.equals(this.status, paymentRejectedResponse.status)
				&& Objects.equals(this.reason, paymentRejectedResponse.reason)
				&& Objects.equals(this.reasonCode, paymentRejectedResponse.reasonCode);
	}

	@Override
	public int hashCode() {
		return Objects.hash(status, reason, reasonCode);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class PaymentResponse {\n");

		sb.append("    status: ").append(toIndentedString(status)).append("\n");
		sb.append("    reason: ").append(toIndentedString(reason)).append("\n");
		sb.append("    reasonCode: ").append(toIndentedString(reasonCode)).append("\n");
		sb.append("}");
		return sb.toString();
	}

	/**
	 * Convert the given object to string with each line indented by 4 spaces
	 * (except the first line).
	 */
	private String toIndentedString(java.lang.Object o) {
		if (o == null) {
			return "null";
		}
		return o.toString().replace("\n", "\n    ");
	}

}
