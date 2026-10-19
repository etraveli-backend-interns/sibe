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

import com.etraveli.ibe.sibe.entities.Flight;
import com.etraveli.ibe.sibe.repository.FlightDAO;
import com.etraveli.ibe.sibe.repository.FlightSearchCriteria;

@Repository
public class FlightDAOJDBC extends AbstractSpringDaoJdbc implements FlightDAO {

  private static final String flight_SELECT_FROM = """
    SELECT id, flight_number, airline, origin, destination, departure_time, arrival_time,
           price, currency, total_seats, available_seats
      FROM flight
    """;

  public FlightDAOJDBC(final JdbcTemplate jdbcTemplate) {
    super(jdbcTemplate);
  }

  @Override
  public List<Flight> getFlights(final FlightSearchCriteria criteria) {
    final String sortDirection = "desc".equalsIgnoreCase(criteria.sortDir()) ? "DESC" : "ASC";

    final List<Object> args = new ArrayList<>();
    final String where = buildWhereClause(criteria, args);
    args.add(criteria.limit());
    args.add(criteria.offset());

    final String sql = flight_SELECT_FROM + where + " ORDER BY " + criteria.sortBy() + " " + sortDirection + ", id LIMIT ? OFFSET ?";
    return getMainJdbcTemplate().query(sql, new FlightRowMapper(), args.toArray());
  }

  @Override
  public long countFlights(final FlightSearchCriteria criteria) {
    final List<Object> args = new ArrayList<>();
    final String where = buildWhereClause(criteria, args);
    return getMainJdbcTemplate().queryForObject("SELECT COUNT(*) FROM flight" + where, Long.class, args.toArray());
  }

  @Override
  public @Nullable Flight getFlight(final long id) {
    try {
      final String sql = flight_SELECT_FROM + " WHERE id = ?";
      return getMainJdbcTemplate().queryForObject(sql, new FlightRowMapper(), id);
    } catch (EmptyResultDataAccessException e) {
      return null;
    }
  }

  @Override
  public boolean reserveSeats(final long flightId, final int seats) {
    final String sql = "UPDATE flight SET available_seats = available_seats - ? WHERE id = ? AND available_seats >= ?";
    return getMainJdbcTemplate().update(sql, seats, flightId, seats) == 1;
  }

  @Override
  public void releaseSeats(final long flightId, final int seats) {
    getMainJdbcTemplate().update("UPDATE flight SET available_seats = available_seats + ? WHERE id = ?", seats, flightId);
  }

  private static String buildWhereClause(final FlightSearchCriteria criteria, final List<Object> args) {
    final List<String> conditions = new ArrayList<>();

    conditions.add("departure_time > ?");
    args.add(criteria.departingAfter());

    conditions.add("available_seats >= ?");
    args.add(criteria.minAvailableSeats());

    if (criteria.origin() != null) {
      conditions.add("origin = ?");
      args.add(criteria.origin());
    }
    if (criteria.destination() != null) {
      conditions.add("destination = ?");
      args.add(criteria.destination());
    }
    if (criteria.departureDate() != null) {
      conditions.add("departure_time >= ? AND departure_time < ?");
      args.add(criteria.departureDate().atStartOfDay());
      args.add(criteria.departureDate().plusDays(1).atStartOfDay());
    }
    if (criteria.minPrice() != null) {
      conditions.add("price >= ?");
      args.add(criteria.minPrice());
    }
    if (criteria.maxPrice() != null) {
      conditions.add("price <= ?");
      args.add(criteria.maxPrice());
    }
    // TODO(intern): add more filters here (airline, max flight duration, departure time-of-day window)

    return " WHERE " + String.join(" AND ", conditions);
  }

  private static class FlightRowMapper implements RowMapper<Flight> {

    @Override
    public Flight mapRow(final ResultSet rs, final int rowNum) throws SQLException {
      return new Flight(
        rs.getLong("id"),
        rs.getString("flight_number"),
        rs.getString("airline"),
        rs.getString("origin"),
        rs.getString("destination"),
        rs.getObject("departure_time", LocalDateTime.class),
        rs.getObject("arrival_time", LocalDateTime.class),
        rs.getBigDecimal("price"),
        rs.getString("currency"),
        rs.getInt("total_seats"),
        rs.getInt("available_seats")
      );
    }
  }
}
