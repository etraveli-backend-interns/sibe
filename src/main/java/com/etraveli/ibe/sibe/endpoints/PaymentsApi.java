package com.etraveli.ibe.sibe.endpoints;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.etraveli.ibe.sibe.dto.PaymentRequest;
import com.etraveli.ibe.sibe.dto.PaymentResponse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;

@RequestMapping("/bookings/{reference}/payments")
public interface PaymentsApi {

  /**
   * {@code POST /bookings/{reference}/payments}: pays a booking. The request body is JSON. This is a fake payment:
   * it is approved when the currency matches and the amount covers the booking total.
   *
   * @param reference the six-character booking reference taken from the URL path
   * @param request   the amount, currency and card number, validated before the method runs
   * @return 201 with the approved payment; 400 if the request is invalid, the booking cannot be paid, or the payment
   *         is declined; 404 if the booking does not exist
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  PaymentResponse pay(
    @PathVariable @Pattern(regexp = "[A-Za-z0-9]{6}") String reference,
    @Valid @RequestBody PaymentRequest request
  );
}
