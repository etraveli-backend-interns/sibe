package com.etraveli.ibe.sibe.endpoints.impl;

import org.springframework.web.bind.annotation.RestController;

import com.etraveli.ibe.sibe.dto.PaymentRequest;
import com.etraveli.ibe.sibe.dto.PaymentResponse;
import com.etraveli.ibe.sibe.endpoints.PaymentsApi;
import com.etraveli.ibe.sibe.service.PaymentService;

@RestController
public class PaymentsApiImpl implements PaymentsApi {

  private final PaymentService paymentService;

  public PaymentsApiImpl(PaymentService paymentService) {
    this.paymentService = paymentService;
  }

  @Override
  public PaymentResponse pay(String reference, PaymentRequest request) {
    return paymentService.pay(reference.toUpperCase(), request);
  }
}
