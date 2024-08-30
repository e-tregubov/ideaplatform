package org.example;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import com.google.gson.*;

class Ticket {
    String origin, origin_name, destination, destination_name, departure_date, departure_time, arrival_date, arrival_time, carrier;
    int price, stops;
}

class Tickets { List<Ticket> tickets; }

public class Main {
    private static final String ORIGIN = "VVO";
    private static final String DESTINATION = "TLV";

    public static void main(String[] args) {
        Gson gson = new Gson();
        try {
            Tickets tickets = gson.fromJson(new FileReader("tickets.json"), Tickets.class);
            Map<String, Integer> minFlightTimes = new HashMap<>();
            List<Integer> allPrices = new ArrayList<>();

            for (Ticket ticket : tickets.tickets) {
                if (ticket.origin.equals(ORIGIN) && ticket.destination.equals(DESTINATION)) {
                    int flightTime = calculateFlightTime(ticket.departure_time, ticket.arrival_time);
                    minFlightTimes.put(ticket.carrier, Math.min(minFlightTimes.getOrDefault(ticket.carrier, Integer.MAX_VALUE), flightTime));
                    allPrices.add(ticket.price);
                }
            }

            System.out.println("Минимальное время полета для каждого авиаперевозчика:");
            minFlightTimes.forEach((carrier, time) -> System.out.println(carrier + ": " + time + " минут"));

            double averagePrice = allPrices.stream().mapToInt(Integer::intValue).average().orElse(0);
            double medianPrice = calculateMedian(allPrices);
            System.out.println("\nРазница между общей средней ценой и медианой: " + (averagePrice - medianPrice));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int calculateFlightTime(String departureTime, String arrivalTime) {
        String[] dep = departureTime.split(":");
        String[] arr = arrivalTime.split(":");
        int depMinutes = Integer.parseInt(dep[0]) * 60 + Integer.parseInt(dep[1]);
        int arrMinutes = Integer.parseInt(arr[0]) * 60 + Integer.parseInt(arr[1]);
        return arrMinutes - depMinutes;
    }

    private static double calculateMedian(List<Integer> prices) {
        Collections.sort(prices);
        int size = prices.size();
        return (size % 2 == 0) ?
                (prices.get(size / 2 - 1) + prices.get(size / 2)) / 2.0 :
                prices.get(size / 2);
    }
}