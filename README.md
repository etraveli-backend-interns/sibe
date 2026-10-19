# Mini-IBE - backend training service

A deliberately small stand-in for the IBE monolith, used in the backend part of the intern training.
Spring Boot + plain JDBC (`NamedParameterJdbcTemplate`, no JPA) + MySQL. Domain: air ticket booking.

## Run it

1. MySQL (Docker) must be listening on `localhost:3307` with database `sibe`, user `root`, password `pwd1`
   (see `src/main/resources/application.properties`).
2. `./gradlew bootRun` - Flyway creates the tables and seeds the flights on first start
   (`src/main/resources/db/migration`). To start over run `DROP DATABASE sibe; CREATE DATABASE sibe;`.
3. The API is served at `http://localhost:8080/etraveli`.

## Layers

```
endpoints/   (controller)  API interfaces: URLs, HTTP annotations, request validation (Bean Validation), javadoc.
endpoints/impl/            Controller implementations (@RestController): call the service. No business rules, no SQL.
service/                   Service interfaces: the business operations, documented with javadoc.
service/impl/              Service implementations: business rules and transactions (@Transactional).
repository/                DAO interfaces only (what the services depend on). See "DAO conventions" below.
repository/jdbc/           DAO implementations: SQL only (JdbcTemplate + RowMappers).
entities/                  Rows as records.        dto/  Request/response records.
exception/                 Domain exceptions + GlobalExceptionHandler (-> RFC 7807 ProblemDetail).
```

## The flow (see `requests/booking-flow.http`, or import `requests/mini-ibe.postman_collection.json` into Postman)

`GET /flights` -> pick a flight `id` -> `POST /bookings` -> booking `reference` -> `POST /bookings/{reference}/payments` -> `GET /bookings/{reference}`

| Endpoint | Description |
|---|---|
| `GET /flights?origin&destination&departureDate&minPrice&maxPrice&passengers&sortBy&sortDir&page&size` | Search flights |
| `GET /flights/{id}` | Flight detail |
| `POST /bookings` | Book a flight for 1-9 passengers (status `CREATED`, seats reserved) |
| `GET /bookings/{reference}` | Booking detail |
| `GET /bookings?status&contactEmail&page&size` | List bookings |
| `POST /bookings/{reference}/payments` | Fake payment: approved when currency matches and amount >= total -> `CONFIRMED`; otherwise 400 |
| `DELETE /bookings/{reference}` | Cancel booking (`CANCELLED`, seats released) |

Errors (`exception/GlobalExceptionHandler`): 404 `NotFoundException` (unknown flight/booking), 400 `InvalidRequestException` (bad input, not enough seats, wrong booking status, declined payment, ...) and Bean Validation failures, 500 anything unexpected.

## DAO conventions (copied from IBE)

- One interface per table group, `FooDAO`, in the `repository` package, and one implementation `FooDAOJDBC extends AbstractSpringDaoJdbc` in the separate `repository.jdbc` package (IBE's newer modules do the same: `...api.provider.dao` vs `...impl.dao.jdbc`). Services depend only on the interface.
- `JdbcTemplate` with positional `?` parameters, obtained through `getMainJdbcTemplate()`. No ORM.
- SQL is inline in the DAO as constants named `<table>_SELECT_FROM`, `<table>_INSERT` (text blocks), snake_case tables and columns, primary key called `id`.
- Row mapping with a `private static class FooRowMapper implements RowMapper<Foo>` inside the DAO.
- Method names: `getX` / `getXs`, `insertX` (returns the generated `long` id), `updateX`, `countXs`.
- A single-row lookup returns `null` when nothing is found (annotated `@Nullable`), never `Optional`. Lists are never null.
- Parameters are `final`; ids are primitive `long`.
- Transactions are declared on the service methods, not in the DAOs.

Differences from IBE: Spring `@Repository` instead of CDI `@ApplicationScoped`, Flyway instead of Liquibase, and no `SQLUtil` helpers (plain `rs.getXxx`). IBE also uses plural table names (`credit_requests`) and `created`/`last_modified` audit columns; here the tables are singular.

## Things to look at

- `FlightDAOJDBC.reserveSeats`: one atomic `UPDATE ... WHERE available_seats >= ?` instead of read-then-write.
- `FlightDAOJDBC.getFlights`: dynamic WHERE clause, sorting and paging.
- `BookingDAOJDBC.updateBookingStatus`: compare-and-set on the status.
- `PaymentService.pay`: `noRollbackFor` - why must the declined payment survive the exception?

Search the code for `TODO(intern)` for places meant to be extended.
