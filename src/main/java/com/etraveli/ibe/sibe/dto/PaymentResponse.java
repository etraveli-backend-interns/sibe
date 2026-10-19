package com.etraveli.ibe.sibe.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.etraveli.ibe.sibe.entities.Payment;
import com.etraveli.ibe.sibe.entities.PaymentStatus;

public record PaymentResponse(Long id, BigDecimal amount, String currency, PaymentStatus status, LocalDateTime createdAt) {

  public static PaymentResponse from(Payment payment) {
    return new PaymentResponse(payment.id(), payment.amount(), payment.currency(), payment.status(), payment.createdAt());
  }
}
