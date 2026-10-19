package com.etraveli.ibe.sibe.repository.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.etraveli.ibe.sibe.entities.Passenger;
import com.etraveli.ibe.sibe.repository.PassengerDAO;

@Repository
public class PassengerDAOJDBC extends AbstractSpringDaoJdbc implements PassengerDAO {

  private static final String passenger_INSERT = """
    INSERT INTO passenger (booking_id, first_name, last_name, date_of_birth, passport_number)
    VALUES (?, ?, ?, ?, ?)
    """;

  private static final String passenger_SELECT_FROM = """
    SELECT id, booking_id, first_name, last_name, date_of_birth, passport_number
      FROM passenger
    """;

  public PassengerDAOJDBC(final JdbcTemplate jdbcTemplate) {
    super(jdbcTemplate);
  }

  @Override
  public void insertPassengers(final long bookingId, final List<Passenger> passengers) {
    getMainJdbcTemplate().batchUpdate(
      passenger_INSERT,
      new BatchPreparedStatementSetter() {
        @Override
        public void setValues(final PreparedStatement ps, final int i) throws SQLException {
          final Passenger passenger = passengers.get(i);
          int idx = 0;
          ps.setLong(++idx, bookingId);
          ps.setString(++idx, passenger.firstName());
          ps.setString(++idx, passenger.lastName());
          ps.setObject(++idx, passenger.dateOfBirth());
          ps.setString(++idx, passenger.passportNumber());
        }

        @Override
        public int getBatchSize() {
          return passengers.size();
        }
      }
    );
  }

  @Override
  public List<Passenger> getPassengers(final long bookingId) {
    final String sql = passenger_SELECT_FROM + " WHERE booking_id = ? ORDER BY id";
    return getMainJdbcTemplate().query(sql, new PassengerRowMapper(), bookingId);
  }

  @Override
  public int countPassengers(final long bookingId) {
    return getMainJdbcTemplate().queryForObject("SELECT COUNT(*) FROM passenger WHERE booking_id = ?", Integer.class, bookingId);
  }

  private static class PassengerRowMapper implements RowMapper<Passenger> {

    @Override
    public Passenger mapRow(final ResultSet rs, final int rowNum) throws SQLException {
      return new Passenger(
        rs.getLong("id"),
        rs.getLong("booking_id"),
        rs.getString("first_name"),
        rs.getString("last_name"),
        rs.getObject("date_of_birth", LocalDate.class),
        rs.getString("passport_number")
      );
    }
  }
}
