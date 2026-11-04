# Mini-IBE - backend training service

A deliberately small stand-in for the IBE monolith, used in the backend part of the intern training.
Spring Boot + plain JDBC (`NamedParameterJdbcTemplate`, no JPA) + MySQL. Domain: air ticket booking.

## Setup

Install once:

- **JDK 21** (e.g. Temurin). Check with `java -version`. Gradle uses the Java 21 toolchain from `build.gradle`.
- **Docker Desktop** (or any Docker engine with the `docker compose` plugin). Make sure it is running.
- **IntelliJ IDEA** (Community is enough for the Java side; the `.http` client needs Ultimate, otherwise use the Postman collection).

Then:

1. Start MySQL from the project root: `docker compose up -d`. It listens on `localhost:3307` with database `sibe`,
   user `root`, password `pwd1` (matching `src/main/resources/application.properties`). Stop it with `docker compose down`.
2. Open the project in IntelliJ: *File > Open* and pick the project folder (`build.gradle`); let Gradle import finish.
   Set *Project Structure > SDK* and *Settings > Build Tools > Gradle > Gradle JVM* to JDK 21.
3. Run `SibeApplication` from IntelliJ, or `./gradlew bootRun` from a terminal.
4. Try the API with `requests/booking-flow.http` (IntelliJ) or the Postman collection in `requests/`.

## Run it

1. MySQL must be up (see Setup above): `localhost:3307`, database `sibe`, user `root`, password `pwd1`.
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

