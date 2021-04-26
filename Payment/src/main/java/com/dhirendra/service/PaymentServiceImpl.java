package com.dhirendra.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.dhirendra.exception.PaymentException;
import com.dhirendra.model.ApiResponseMessage;
import com.dhirendra.model.PaymentInitiationRequest;
import com.dhirendra.model.PaymentResponse;
import com.dhirendra.model.TransactionStatus;
import com.dhirendra.util.PaymentUtil;

/**
 * 
 * This service class checks the amount limit by implementing PaymentService
 * interface
 * 
 */

@Service
public class PaymentServiceImpl implements PaymentService {

	/**
	 * 
	 * @param paymentInitiationRequest
	 * @return
	 * @throws PaymentException
	 */

	public PaymentResponse paymentLimitCheck(PaymentInitiationRequest paymentInitiationRequest)
			throws PaymentException {

		PaymentResponse paymentResponse;

		try {

			Double amount = Double.parseDouble(paymentInitiationRequest.getAmount());

			if ((amount > 0) && (PaymentUtil.sum(paymentInitiationRequest.getDebtorIBAN()))
					% ((paymentInitiationRequest.getDebtorIBAN().length())) == 0) {

				throw new PaymentException(ApiResponseMessage.ERROR, "limit exceeded");

			}

			paymentResponse = new PaymentResponse();
			paymentResponse.setPaymentId(UUID.randomUUID().toString());
			paymentResponse.setStatus(TransactionStatus.ACCEPTED);

		}

		catch (PaymentException e) {
			throw new PaymentException(ApiResponseMessage.ERROR, "limit exceeded");

		}
		return paymentResponse;

	}
}
