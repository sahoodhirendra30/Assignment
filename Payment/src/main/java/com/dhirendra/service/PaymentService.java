package com.dhirendra.service;

import org.springframework.stereotype.Service;

import com.dhirendra.exception.PaymentException;
import com.dhirendra.model.PaymentInitiationRequest;
import com.dhirendra.model.PaymentResponse;

@Service
public interface PaymentService {
	
	public PaymentResponse paymentLimitCheck(PaymentInitiationRequest paymentInitiationRequest) throws PaymentException;

}
