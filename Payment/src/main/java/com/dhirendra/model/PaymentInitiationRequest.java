package com.dhirendra.model;

import java.io.Serializable;
import java.util.Objects;

import javax.validation.constraints.Pattern;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder.Default;
import lombok.Data;

@Validated
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class PaymentInitiationRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonProperty("debtorIBAN")
	@Pattern(regexp = "[A-Z]{2}[0-9]{2}[a-zA-Z0-9]{1,30}", message = "Invalid iban number" )
	private String debtorIBAN = null;

	@JsonProperty("creditorIBAN")
	@Pattern(regexp = "[A-Z]{2}[0-9]{2}[a-zA-Z0-9]{1,30}")
	private String creditorIBAN = null;

	@JsonProperty("amount")

	private String amount = null;

	@JsonProperty("currency")	
	@Pattern(regexp = "[A-Z]{3}")
	private String currency = "EUR";

	@JsonProperty("endToEndId")
	private String endToEndId = null;

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		PaymentInitiationRequest paymentInitiationRequest = (PaymentInitiationRequest) o;
		return Objects.equals(this.debtorIBAN, paymentInitiationRequest.debtorIBAN)
				&& Objects.equals(this.creditorIBAN, paymentInitiationRequest.creditorIBAN)
				&& Objects.equals(this.amount, paymentInitiationRequest.amount)
				&& Objects.equals(this.currency, paymentInitiationRequest.currency)
				&& Objects.equals(this.endToEndId, paymentInitiationRequest.endToEndId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(debtorIBAN, creditorIBAN, amount, currency, endToEndId);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class PaymentInitiationRequest {\n");

		sb.append("    debtorIBAN: ").append(toIndentedString(debtorIBAN)).append("\n");
		sb.append("    creditorIBAN: ").append(toIndentedString(creditorIBAN)).append("\n");
		sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
		sb.append("    currency: ").append(toIndentedString(currency)).append("\n");
		sb.append("    endToEndId: ").append(toIndentedString(endToEndId)).append("\n");
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

	public PaymentInitiationRequest(@Pattern(regexp = "[A-Z]{2}[0-9]{2}[a-zA-Z0-9]{1,30}") String debtorIBAN,
			@Pattern(regexp = "[A-Z]{2}[0-9]{2}[a-zA-Z0-9]{1,30}") String creditorIBAN, String amount,
			@Pattern(regexp = "[A-Z]{3}") String currency, String endToEndId) {
		super();
		this.debtorIBAN = debtorIBAN;
		this.creditorIBAN = creditorIBAN;
		this.amount = amount;
		this.currency = currency;
		this.endToEndId = endToEndId;
	}

	public PaymentInitiationRequest() {
		super();
		// TODO Auto-generated constructor stub
	}

}
