package com.etraveli.ibe.sibe.service;

import com.etraveli.ibe.sibe.dto.PaymentRequest;
import com.etraveli.ibe.sibe.dto.PaymentResponse;
import com.etraveli.ibe.sibe.exception.InvalidRequestException;
import com.etraveli.ibe.sibe.exception.NotFoundException;

public interface PaymentService {

  /**
   * Pays a booking. This is a fake payment provider: the payment is approved when the currency matches the booking
   * and the amount covers the booking total. An approved payment moves the booking to {@code CONFIRMED}.
   * A declined attempt is still stored on the booking, with status {@code DECLINED}, before the failure is reported.
   *
   * @param reference the booking reference
   * @param request   the amount, currency and card details
   * @return the approved payment
   * @throws NotFoundException       if there is no booking with this reference
   * @throws InvalidRequestException if the booking is not in status {@code CREATED}, if the payment is declined, or
   *                                 if the booking was changed by someone else at the same time
   */
  PaymentResponse pay(String reference, PaymentRequest request);
}
