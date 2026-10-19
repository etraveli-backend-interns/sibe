package com.etraveli.ibe.sibe.service.impl;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.etraveli.ibe.sibe.dto.BookingResponse;
import com.etraveli.ibe.sibe.dto.BookingSummaryResponse;
import com.etraveli.ibe.sibe.dto.CreateBookingRequest;
import com.etraveli.ibe.sibe.dto.PageResponse;
import com.etraveli.ibe.sibe.entities.Booking;
import com.etraveli.ibe.sibe.entities.BookingStatus;
import com.etraveli.ibe.sibe.entities.Flight;
import com.etraveli.ibe.sibe.entities.Passenger;
import com.etraveli.ibe.sibe.exception.InvalidRequestException;
import com.etraveli.ibe.sibe.exception.NotFoundException;
import com.etraveli.ibe.sibe.repository.BookingDAO;
import com.etraveli.ibe.sibe.repository.FlightDAO;
import com.etraveli.ibe.sibe.repository.PassengerDAO;
import com.etraveli.ibe.sibe.repository.PaymentDAO;
import com.etraveli.ibe.sibe.service.BookingService;

@Service
public class BookingServiceImpl implements BookingService {

  private static final String REFERENCE_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
  private static final int REFERENCE_LENGTH = 6;
  private static final int MAX_REFERENCE_ATTEMPTS = 5;

  private final SecureRandom random = new SecureRandom();

  private final FlightDAO flightDAO;
  private final BookingDAO bookingDAO;
  private final PassengerDAO passengerDAO;
  private final PaymentDAO paymentDAO;

  public BookingServiceImpl(FlightDAO flightDAO, BookingDAO bookingDAO, PassengerDAO passengerDAO, PaymentDAO paymentDAO) {
    this.flightDAO = flightDAO;
    this.bookingDAO = bookingDAO;
    this.passengerDAO = passengerDAO;
    this.paymentDAO = paymentDAO;
  }

  @Override
  @Transactional
  public BookingResponse createBooking(CreateBookingRequest request) {
    Flight flight = flightDAO.getFlight(request.flightId());
    if (flight == null) {
      throw new NotFoundException("Flight " + request.flightId() + " not found");
    }
    //validations could be done by interns
    //perhaps validate duplicate passports
    if (!flight.departureTime().isAfter(LocalDateTime.now())) {
      throw new InvalidRequestException("Flight " + flight.flightNumber() + " has already departed");
    }

    int passengerCount = request.passengers().size();
    if (!flightDAO.reserveSeats(flight.id(), passengerCount)) {
      throw new InvalidRequestException("Flight " + flight.id() + " does not have " + passengerCount + " seat(s) available");
    }

    // TODO(intern): every passenger is priced as an adult - add infant/child fares based on dateOfBirth
    BigDecimal totalAmount = flight.price().multiply(BigDecimal.valueOf(passengerCount));

    LocalDateTime now = LocalDateTime.now();
    Booking booking = insertWithUniqueReference(
      new Booking(null, null, flight.id(), BookingStatus.CREATED, totalAmount, flight.currency(), request.contactEmail(), now, now)
    );

    List<Passenger> passengers = request
      .passengers()
      .stream()
      .map(p -> new Passenger(null, booking.id(), p.firstName(), p.lastName(), p.dateOfBirth(), p.passportNumber().toUpperCase()))
      .toList();
    passengerDAO.insertPassengers(booking.id(), passengers);

    return toResponse(booking);
  }

  @Override
  public BookingResponse getBooking(String reference) {
    return toResponse(findBookingOrThrow(reference));
  }

  @Override
  public PageResponse<BookingSummaryResponse> listBookings(BookingStatus status, String contactEmail, int page, int size) {
    List<BookingSummaryResponse> items = bookingDAO
      .getBookings(status, contactEmail, page * size, size)
      .stream()
      .map(BookingSummaryResponse::from)
      .toList();
    long total = bookingDAO.countBookings(status, contactEmail);
    return PageResponse.of(items, page, size, total);
  }

  @Override
  @Transactional
  public BookingResponse cancelBooking(String reference) {
    Booking booking = findBookingOrThrow(reference);

    if (booking.status() == BookingStatus.CANCELLED) {
      throw new InvalidRequestException("Booking " + reference + " is already cancelled");
    }
    if (!bookingDAO.updateBookingStatus(booking.id(), booking.status(), BookingStatus.CANCELLED)) {
      throw new InvalidRequestException("Booking " + reference + " was modified concurrently, please retry");
    }

    flightDAO.releaseSeats(booking.flightId(), passengerDAO.countPassengers(booking.id()));
    // TODO(intern): a CONFIRMED booking has been paid - create a refund when cancelling it

    return toResponse(findBookingOrThrow(reference));
  }

  private Booking findBookingOrThrow(String reference) {
    Booking booking = bookingDAO.getBookingByReference(reference);
    if (booking == null) {
      throw new NotFoundException("Booking " + reference + " not found");
    }
    return booking;
  }

  private BookingResponse toResponse(Booking booking) {
    Flight flight = flightDAO.getFlight(booking.flightId());
    if (flight == null) {
      throw new NotFoundException("Flight " + booking.flightId() + " not found");
    }
    return BookingResponse.from(
      booking,
      flight,
      passengerDAO.getPassengers(booking.id()),
      paymentDAO.getPayments(booking.id())
    );
  }

  private Booking insertWithUniqueReference(Booking template) {
    for (int attempt = 0; attempt < MAX_REFERENCE_ATTEMPTS; attempt++) {
      Booking candidate = new Booking(
        null,
        generateReference(),
        template.flightId(),
        template.status(),
        template.totalAmount(),
        template.currency(),
        template.contactEmail(),
        template.createdAt(),
        template.updatedAt()
      );
      try {
        return candidate.withId(bookingDAO.insertBooking(candidate));
      } catch (DuplicateKeyException e) {
        // reference collision - generate another one
      }
    }
    throw new IllegalStateException("Could not generate a unique booking reference");
  }

  private String generateReference() {
    StringBuilder reference = new StringBuilder(REFERENCE_LENGTH);
    for (int i = 0; i < REFERENCE_LENGTH; i++) {
      reference.append(REFERENCE_ALPHABET.charAt(random.nextInt(REFERENCE_ALPHABET.length())));
    }
    return reference.toString();
  }
}
