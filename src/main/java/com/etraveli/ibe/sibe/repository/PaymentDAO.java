package com.etraveli.ibe.sibe.repository;

import java.util.List;

import com.etraveli.ibe.sibe.entities.Payment;

public interface PaymentDAO {

  /** Inserts the payment and returns the generated id. */
  long insertPayment(Payment payment);

  /** Returns the payments of the booking, oldest first, not null. */
  List<Payment> getPayments(long bookingId);
}
