package com.etraveli.ibe.sibe.repository.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.etraveli.ibe.sibe.entities.Payment;
import com.etraveli.ibe.sibe.entities.PaymentStatus;
import com.etraveli.ibe.sibe.repository.PaymentDAO;

@Repository
public class PaymentDAOJDBC extends AbstractSpringDaoJdbc implements PaymentDAO {

  private static final String payment_INSERT = """
    INSERT INTO payment (booking_id, amount, currency, status, created_at)
    VALUES (?, ?, ?, ?, ?)
    """;

  private static final String payment_SELECT_FROM = """
    SELECT id, booking_id, amount, currency, status, created_at
      FROM payment
    """;

  public PaymentDAOJDBC(final JdbcTemplate jdbcTemplate) {
    super(jdbcTemplate);
  }

  @Override
  public long insertPayment(final Payment payment) {
    return insertReturnsGeneratedId(
      getMainJdbcTemplate(),
      payment_INSERT,
      payment.bookingId(),
      payment.amount(),
      payment.currency(),
      payment.status().name(),
      payment.createdAt()
    );
  }

  @Override
  public List<Payment> getPayments(final long bookingId) {
    final String sql = payment_SELECT_FROM + " WHERE booking_id = ? ORDER BY id";
    return getMainJdbcTemplate().query(sql, new PaymentRowMapper(), bookingId);
  }

  private static class PaymentRowMapper implements RowMapper<Payment> {

    @Override
    public Payment mapRow(final ResultSet rs, final int rowNum) throws SQLException {
      return new Payment(
        rs.getLong("id"),
        rs.getLong("booking_id"),
        rs.getBigDecimal("amount"),
        rs.getString("currency"),
        PaymentStatus.valueOf(rs.getString("status")),
        rs.getObject("created_at", LocalDateTime.class)
      );
    }
  }
}
