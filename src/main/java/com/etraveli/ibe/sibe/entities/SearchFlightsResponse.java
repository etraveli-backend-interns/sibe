package com.etraveli.ibe.sibe.entities;

import java.util.List;

public class SearchFlightsResponse {

    private final List<Flight> listOfFlights;
    private final long totalFlights;

    public SearchFlightsResponse(List<Flight> listOfFlights, long totalFlights) {
        this.listOfFlights = listOfFlights;
        this.totalFlights = totalFlights;
    }

    public long getTotalFlights() {
        return totalFlights;
    }

    public List<Flight> getListOfFlights() {
        return listOfFlights;
    }

}


