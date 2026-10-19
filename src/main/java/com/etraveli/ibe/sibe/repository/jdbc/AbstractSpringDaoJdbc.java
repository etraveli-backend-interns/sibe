package com.etraveli.ibe.sibe.repository.jdbc;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Objects;

import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

/** Scaled-down version of IBE's AbstractSpringDaoJdbc: gives DAOs their JdbcTemplate and common helpers. */
public abstract class AbstractSpringDaoJdbc {

  private final JdbcTemplate mainJdbcTemplate;

  protected AbstractSpringDaoJdbc(final JdbcTemplate mainJdbcTemplate) {
    this.mainJdbcTemplate = mainJdbcTemplate;
  }

  protected JdbcTemplate getMainJdbcTemplate() {
    return mainJdbcTemplate;
  }

  /** Runs the insert and returns the generated key. */
  protected static long insertReturnsGeneratedId(final JdbcTemplate jdbcTemplate, final String sql, final Object... args) {
    final KeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(
      connection -> {
        final PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        new ArgumentPreparedStatementSetter(args).setValues(ps);
        return ps;
      },
      keyHolder
    );
    return Objects.requireNonNull(keyHolder.getKey()).longValue();
  }
}
