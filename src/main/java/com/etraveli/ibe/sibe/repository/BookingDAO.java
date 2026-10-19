package com.etraveli.ibe.sibe.repository;

import java.util.List;

import org.jspecify.annotations.Nullable;

import com.etraveli.ibe.sibe.entities.Booking;
import com.etraveli.ibe.sibe.entities.BookingStatus;

public interface BookingDAO {

  /**
   * Inserts the booking and returns the generated id.
   *
   * @throws org.springframework.dao.DuplicateKeyException if the reference is already taken.
   */
  long insertBooking(Booking booking);

  /** @return null if there is none for the reference. */
  @Nullable
  Booking getBookingByReference(String reference);

  /** Returns one page of bookings, newest first. Null filters are ignored. */
  List<Booking> getBookings(@Nullable BookingStatus status, @Nullable String contactEmail, int offset, int limit);

  long countBookings(@Nullable BookingStatus status, @Nullable String contactEmail);

  /** Compare-and-set: moves the booking only if it still has the expected status. @return false otherwise. */
  boolean updateBookingStatus(long bookingId, BookingStatus expected, BookingStatus newStatus);
}
