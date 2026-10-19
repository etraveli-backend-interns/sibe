package com.etraveli.ibe.sibe.repository.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.etraveli.ibe.sibe.entities.Booking;
import com.etraveli.ibe.sibe.entities.BookingStatus;
import com.etraveli.ibe.sibe.repository.BookingDAO;

@Repository
public class BookingDAOJDBC extends AbstractSpringDaoJdbc implements BookingDAO {

  private static final String booking_INSERT = """
    INSERT INTO booking (reference, flight_id, status, total_amount, currency, contact_email, created_at, updated_at)
    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
    """;

  private static final String booking_SELECT_FROM = """
    SELECT id, reference, flight_id, status, total_amount, currency, contact_email, created_at, updated_at
      FROM booking
    """;

  public BookingDAOJDBC(final JdbcTemplate jdbcTemplate) {
    super(jdbcTemplate);
  }

  @Override
  public long insertBooking(final Booking booking) {
    return insertReturnsGeneratedId(
      getMainJdbcTemplate(),
      booking_INSERT,
      booking.reference(),
      booking.flightId(),
      booking.status().name(),
      booking.totalAmount(),
      booking.currency(),
      booking.contactEmail(),
      booking.createdAt(),
      booking.updatedAt()
    );
  }

  @Override
  public @Nullable Booking getBookingByReference(final String reference) {
    try {
      final String sql = booking_SELECT_FROM + " WHERE reference = ?";
      return getMainJdbcTemplate().queryForObject(sql, new BookingRowMapper(), reference);
    } catch (EmptyResultDataAccessException e) {
      return null;
    }
  }

  @Override
  public List<Booking> getBookings(
    final @Nullable BookingStatus status,
    final @Nullable String contactEmail,
    final int offset,
    final int limit
  ) {
    final List<Object> args = new ArrayList<>();
    final String where = buildWhereClause(status, contactEmail, args);
    args.add(limit);
    args.add(offset);
    final String sql = booking_SELECT_FROM + where + " ORDER BY created_at DESC, id DESC LIMIT ? OFFSET ?";
    return getMainJdbcTemplate().query(sql, new BookingRowMapper(), args.toArray());
  }

  @Override
  public long countBookings(final @Nullable BookingStatus status, final @Nullable String contactEmail) {
    final List<Object> args = new ArrayList<>();
    final String where = buildWhereClause(status, contactEmail, args);
    return getMainJdbcTemplate().queryForObject("SELECT COUNT(*) FROM booking" + where, Long.class, args.toArray());
  }

  @Override
  public boolean updateBookingStatus(final long bookingId, final BookingStatus expected, final BookingStatus newStatus) {
    final String sql = "UPDATE booking SET status = ?, updated_at = ? WHERE id = ? AND status = ?";
    return getMainJdbcTemplate().update(sql, newStatus.name(), LocalDateTime.now(), bookingId, expected.name()) == 1;
  }

  private static String buildWhereClause(
    final @Nullable BookingStatus status,
    final @Nullable String contactEmail,
    final List<Object> args
  ) {
    final List<String> conditions = new ArrayList<>();
    if (status != null) {
      conditions.add("status = ?");
      args.add(status.name());
    }
    if (contactEmail != null) {
      conditions.add("contact_email = ?");
      args.add(contactEmail);
    }
    return conditions.isEmpty() ? "" : " WHERE " + String.join(" AND ", conditions);
  }

  private static class BookingRowMapper implements RowMapper<Booking> {

    @Override
    public Booking mapRow(final ResultSet rs, final int rowNum) throws SQLException {
      return new Booking(
        rs.getLong("id"),
        rs.getString("reference"),
        rs.getLong("flight_id"),
        BookingStatus.valueOf(rs.getString("status")),
        rs.getBigDecimal("total_amount"),
        rs.getString("currency"),
        rs.getString("contact_email"),
        rs.getObject("created_at", LocalDateTime.class),
        rs.getObject("updated_at", LocalDateTime.class)
      );
    }
  }
}
