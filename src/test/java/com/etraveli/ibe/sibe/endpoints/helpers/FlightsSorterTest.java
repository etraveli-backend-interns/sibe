package com.etraveli.ibe.sibe.endpoints.helpers;

import com.etraveli.ibe.sibe.entities.Flight;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class FlightsSorterTest {

    @Test
    public void test_flightSorting_happyPath() {

        // Arrange

        List<Flight> flightsUnsorted = new ArrayList<>();

        flightsUnsorted.add(createTestFlight(200, null, null));
        flightsUnsorted.add(createTestFlight(400, null, null));
        flightsUnsorted.add(createTestFlight(50, null, null));


        // Act

        List<Flight> sortedFlightList = FlightsSorter.sortFlights("price", flightsUnsorted);

        // Assert
        assertThat(sortedFlightList).isNotNull();
        assertThat(sortedFlightList.getFirst().price()).isEqualTo(BigDecimal.valueOf(50));
    }

    @Test
    public void test_flightSorting_sadgePath() {

        // Arrange

        List<Flight> flightsUnsorted = null;

        // Act

        List<Flight> sortedFlightList = FlightsSorter.sortFlights("price", flightsUnsorted);

        // Assert
        assertThat(sortedFlightList).isNotNull();
        assertThat(sortedFlightList).isEmpty();
    }

    public static Flight createTestFlight(long price, LocalDateTime departureTime, LocalDateTime arrivalTime) {
        return new Flight(null,null,
                null,null, null,departureTime,arrivalTime,
                BigDecimal.valueOf(price), null, 0, 0);
    }
}
