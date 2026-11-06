package com.etraveli.ibe.sibe.endpoints.helpers;

import com.etraveli.ibe.sibe.endpoints.impl.FlightsApiImpl;
import com.etraveli.ibe.sibe.entities.Flight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class FlightsSorter {

    private static final Logger log = LoggerFactory.getLogger(FlightsSorter.class);

    public static List<Flight> sortFlights(String typeOfSorting, List<Flight> unsortedListOfFlights) {

        List<Flight> sortedFlights = new ArrayList<>();

        if(unsortedListOfFlights == null){
            log.warn("Warning: List of Flights is NULL.");
             return sortedFlights;
        }

        switch(typeOfSorting){

            case "price":
                sortedFlights = sortFlights(unsortedListOfFlights,new PriceComparator());
                break;
            case "arrivalTime":
                sortedFlights = sortFlights(unsortedListOfFlights,new ArrivalTimeComparator());
                break;
            case "departureTime" :
                sortedFlights = sortFlights(unsortedListOfFlights,new DepartureTimeComparator());
                break;
            default:
                log.warn("Warning: Unknown sorting parameter.");
        }

        return sortedFlights;
    }

    private static List<Flight> sortFlights(List<Flight> unsortedListOfFlights, Comparator<Flight> comparator){
        return unsortedListOfFlights.stream().sorted(comparator).toList();
    }


}
