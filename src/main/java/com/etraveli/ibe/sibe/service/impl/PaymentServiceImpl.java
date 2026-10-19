package com.etraveli.ibe.sibe.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.etraveli.ibe.sibe.dto.PaymentRequest;
import com.etraveli.ibe.sibe.dto.PaymentResponse;
import com.etraveli.ibe.sibe.entities.Booking;
import com.etraveli.ibe.sibe.entities.BookingStatus;
import com.etraveli.ibe.sibe.entities.Payment;
import com.etraveli.ibe.sibe.entities.PaymentStatus;
import com.etraveli.ibe.sibe.exception.InvalidRequestException;
import com.etraveli.ibe.sibe.exception.NotFoundException;
import com.etraveli.ibe.sibe.repository.BookingDAO;
import com.etraveli.ibe.sibe.repository.PaymentDAO;
import com.etraveli.ibe.sibe.service.PaymentService;

@Service
public class PaymentServiceImpl implements PaymentService {

  private final BookingDAO bookingDAO;
  private final PaymentDAO paymentDAO;

  public PaymentServiceImpl(BookingDAO bookingDAO, PaymentDAO paymentDAO) {
    this.bookingDAO = bookingDAO;
    this.paymentDAO = paymentDAO;
  }

  // noRollbackFor: a declined payment must still be stored, even though we signal it with an exception
  @Override
  @Transactional(noRollbackFor = InvalidRequestException.class)
  public PaymentResponse pay(String reference, PaymentRequest request) {
    Booking booking = bookingDAO.getBookingByReference(reference);
    if (booking == null) {
      throw new NotFoundException("Booking " + reference + " not found");
    }

    if (booking.status() != BookingStatus.CREATED) {
      throw new InvalidRequestException("Booking " + reference + " is " + booking.status() + " and cannot be paid");
    }

    // Fake payment provider: approve when the currency matches and the amount covers the total
    boolean approved =
      booking.currency().equalsIgnoreCase(request.currency()) && request.amount().compareTo(booking.totalAmount()) >= 0;

    if (approved && !bookingDAO.updateBookingStatus(booking.id(), BookingStatus.CREATED, BookingStatus.CONFIRMED)) {
      throw new InvalidRequestException("Booking " + reference + " was modified concurrently, please retry");
    }

    Payment payment = new Payment(
      null,
      booking.id(),
      request.amount(),
      request.currency().toUpperCase(),
      approved ? PaymentStatus.APPROVED : PaymentStatus.DECLINED,
      LocalDateTime.now()
    );
    payment = payment.withId(paymentDAO.insertPayment(payment));

    if (!approved) {
      throw new InvalidRequestException(
        "Payment declined: booking total is " + booking.totalAmount() + " " + booking.currency()
      );
    }
    // TODO(intern): overpayments are accepted as-is - decide what to do with the difference

    return PaymentResponse.from(payment);
  }
}
