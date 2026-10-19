CREATE TABLE flight (
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  flight_number   VARCHAR(10)   NOT NULL,
  airline         VARCHAR(50)   NOT NULL,
  origin          CHAR(3)       NOT NULL,
  destination     CHAR(3)       NOT NULL,
  departure_time  DATETIME      NOT NULL,
  arrival_time    DATETIME      NOT NULL,
  price           DECIMAL(10,2) NOT NULL,
  currency        CHAR(3)       NOT NULL,
  total_seats     INT           NOT NULL,
  available_seats INT           NOT NULL,
  CONSTRAINT chk_flight_seats CHECK (available_seats >= 0 AND available_seats <= total_seats),
  INDEX idx_flight_search (origin, destination, departure_time)
);

CREATE TABLE booking (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  reference     CHAR(6)       NOT NULL,
  flight_id     BIGINT        NOT NULL,
  status        VARCHAR(10)   NOT NULL,
  total_amount  DECIMAL(10,2) NOT NULL,
  currency      CHAR(3)       NOT NULL,
  contact_email VARCHAR(255)  NOT NULL,
  created_at    DATETIME      NOT NULL,
  updated_at    DATETIME      NOT NULL,
  CONSTRAINT uk_booking_reference UNIQUE (reference),
  CONSTRAINT fk_booking_flight FOREIGN KEY (flight_id) REFERENCES flight (id),
  INDEX idx_booking_status (status),
  INDEX idx_booking_email (contact_email)
);

CREATE TABLE passenger (
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  booking_id      BIGINT       NOT NULL,
  first_name      VARCHAR(100) NOT NULL,
  last_name       VARCHAR(100) NOT NULL,
  date_of_birth   DATE         NOT NULL,
  passport_number VARCHAR(20)  NOT NULL,
  CONSTRAINT fk_passenger_booking FOREIGN KEY (booking_id) REFERENCES booking (id)
);

CREATE TABLE payment (
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  booking_id BIGINT        NOT NULL,
  amount     DECIMAL(10,2) NOT NULL,
  currency   CHAR(3)       NOT NULL,
  status     VARCHAR(10)   NOT NULL,
  created_at DATETIME      NOT NULL,
  CONSTRAINT fk_payment_booking FOREIGN KEY (booking_id) REFERENCES booking (id)
);
