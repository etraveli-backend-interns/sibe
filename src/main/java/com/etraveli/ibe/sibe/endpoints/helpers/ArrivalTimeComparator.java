package com.etraveli.ibe.sibe.endpoints.helpers;

import com.etraveli.ibe.sibe.entities.Flight;

import java.util.Comparator;

public class ArrivalTimeComparator implements Comparator<Flight> {

    @Override
    public int compare(Flight flight1, Flight flight2) {
        return flight1.arrivalTime().compareTo(flight2.arrivalTime());
    }
}
